package dungeoncrawler.controllers;

import dungeoncrawler.handlers.Controls;
import dungeoncrawler.objects.Door;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.Entity;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Room;
import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.sql.SQLOutput;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class for running the game. Does all the calculations and stuff.
 *
 * @author Kevin Zhao, Manas Harbola
 * @version 1.0
 */
public class GameController {
    private Timer timer;
    private Controls controls;
    private Room room;
    private Scene scene;
    private Player player;
    private double velX;
    private double velY;
    private double accelX;
    private double accelY;
    private boolean isRunning;
    private long ticks;
    private double totalTime;
    private boolean isAttacking;

    //boolean variables for tracking event
    private boolean pressLeft;
    private boolean pressRight;
    private boolean pressUp;
    private boolean pressDown;
    private boolean frictionX;
    private boolean frictionY;

    /**
     * Constructor for a GameController.
     * @param player Player node
     */
    public GameController(Player player) {
        if (player == null) {
            throw new IllegalArgumentException(
                    "Cannot assign null Player reference to GameController instance"
            );
        }
        this.player = player;
        this.controls = new Controls();
    }

    public GameController() {
        this.controls = new Controls();
    }

    /**
     * Starts the game.
     * @param room Current/first room
     */
    public void start(Room room) {
        //Render room
        setRoom(room);
        scene = Controller.getState().getScene();

        //Handle key events
        scene.setOnKeyPressed(e -> handleKey(e.getCode(), true));
        scene.setOnKeyReleased(e -> handleKey(e.getCode(), false));
    }

    /**
     * Sets the player object of the game.
     * @param player Player node
     */
    public void setPlayer(Player player) {
        this.player = player;
        resetPos();
    }

    /**
     * Changes the room.
     * @param newRoom Room to change to
     */
    private void setRoom(Room newRoom) {
        if (isRunning) {
            pause();
        }
        room = newRoom;
        for (Monster m : room.getMonsters()) {
            System.out.println(m);
        }
        if (Controller.getState() instanceof GameScreen) {
            ((GameScreen) Controller.getState()).setRoom(newRoom);
        } else {
            pause();
            throw new IllegalStateException("Illegal GameState");
        }
    }

    /**
     * Updates data after room change.
     */
    public void updateRoom() {
        reset();
        resetPos();
        pause();
    }

    /**
     * Resets the player's position to the starting position.
     */
    public void resetPos() {
        player.setPosX(room.getStartX());
        player.setPosY(room.getStartY());
        player.getNode().setX(getPx(player.getPosX()));
        player.getNode().setY(getPx(room.getHeight() - player.getPosY() - player.getHeight() * 2));
    }

    /**
     * Resets all game values.
     */
    private void reset() {
        velX = 0.0;
        velY = 0.0;
        accelX = 0.0;
        accelY = 0.0;
        isRunning = false;
        ticks = 0;
        totalTime = 0;
        pressLeft = false;
        pressRight = false;
        pressUp = false;
        pressDown = false;
        frictionX = false;
        frictionY = false;
        resetPos();
    }

    /**
     * Pauses/resumes the game.
     */
    public void pause() {
        if (isRunning) {
            System.out.println("Game has been paused");
            timer.cancel();
        } else {
            System.out.println("Game has been resumed");
            startTimer();
        }
        isRunning = !isRunning;
    }

    /**
     * Starts the game timer/clock.
     */
    private void startTimer() {
        timer = new Timer();
        timer.schedule(new GameRunner(), 0, 1000 / GameSettings.FPS);
    }

    /**
     * Handler for key events.
     * @param keyCode KeyCode of the key that was pressed
     * @param isPress Whether the event is a press or release event
     */
    private void handleKey(KeyCode keyCode, boolean isPress) {
        //Global key binds, regardless of game play/pause state
        String key = keyCode.toString();
        if (key.equals(controls.getKey("pause"))) {
            if (!isPress) {
                pause();
            }
        }

        if (!isRunning) {
            return;
        }
        int sign = 0;
        boolean xval = false;
        if (key.equals(controls.getKey("up"))) {
            if (isPress == pressUp) {
                return;
            }
            sign = isPress ? 1 : -1;
            pressUp = isPress;
        } else if (key.equals(controls.getKey("down"))) {
            if (isPress == pressDown) {
                return;
            }
            sign = isPress ? -1 : 1;
            pressDown = isPress;
        } else if (key.equals(controls.getKey("right"))) {
            if (isPress == pressRight) {
                return;
            }
            sign = isPress ? 1 : -1;
            pressRight = isPress;
            xval = true;
        } else if (key.equals(controls.getKey("left"))) {
            if (isPress == pressLeft) {
                return;
            }
            sign = isPress ? -1 : 1;
            pressLeft = isPress;
            xval = true;
        } else if (key.equals(controls.getKey("attack")) || key.equals(controls.getKey("attack2"))) {
            isAttacking = isPress;
            System.out.println("attack pressed");
        }
        if (xval) {
            accelX += sign * GameSettings.ACCEL;
            accelX = round(accelX);
        } else {
            accelY += sign * GameSettings.ACCEL;
            accelY = round(accelY);
        }
    }

    /**
     * Rounds a number to (precision) digits.
     * @param number Number to round
     * @return Returns the rounded number.
     */
    private double round(double number) {
        return Math.round(number * GameSettings.PRECISION) / GameSettings.PRECISION;
    }

    /**
     * Converts game units to pixels for rendering.
     * @param units Game units
     * @return pixel equivalent
     */
    private double getPx(double units) {
        return units * GameSettings.PPU;
    }

    /**
     * Class that is used to calculate stuff on each tick.
     */
    class GameRunner extends TimerTask {
        /**
         * Primary runner method, controls the data calculations of each tick.
         */
        public void run() {
            ticks++;
            long startTime = System.nanoTime();
            double posX = player.getPosX();
            double posY = player.getPosY();
            double newPosX = round(posX + velX);
            double newPosY = round(posY + velY);

            //check if position is valid. If it is, move.
            double[] movePos = checkPos(posX, posY, newPosX, newPosY, player.getHeight(),
                    player.getWidth());
            if (movePos[0] != posX || movePos[1] != posY) {
                newPosX = movePos[0];
                newPosY = movePos[1];
                movePlayer(newPosX, newPosY);
                //check for door intersections
                if (checkDoors(posX, posY, newPosX, newPosY)) {
                    return;
                }
                player.setPosX(newPosX);
                player.setPosY(newPosY);
            }

            player.setAttackCooldown(Math.max(0.0, player.getAttackCooldown() - 1000.0 / GameSettings.FPS));
            if (isAttacking && player.getAttackCooldown() == 0.0) {
                player.setAttackCooldown(1000.0);
                for (Monster m : room.getMonsters()) {
                    if (m != null && m.getHealth() > 0) {
                        double dist = Math.sqrt(Math.pow(player.getPosX() - m.getPosX(), 2) + Math.pow(player.getPosY() - m.getPosY(), 2));
                        if (dist <= GameSettings.MONSTER_ATTACK_RANGE) {
                            m.attackMonster((int) player.getAttack());
                        }
                    }
                }

            }
            if (player.getAttackCooldown() > 0) {
                System.out.println("Cooldown: " + player.getAttackCooldown());
            }


            //Manage Monsters
            for (Monster m : room.getMonsters()) {
                if (m == null) {
                    continue;
                }
                //check and move the monster
                monsterMove(m);
            }



            //update velocity
            velX += accelX;
            velX = round(velX);
            velY += accelY;
            velY = round(velY);

            //don't allow speed to exceed max
            if (Math.abs(velX) >= GameSettings.MAX_VEL) {
                velX = (velX > 0 ? 1 : -1) * GameSettings.MAX_VEL;
                //was moving before and decelerated to 0
            } else if (frictionX && Math.abs(round(velX - accelX)) < Math.abs(accelX)) {
                velX = 0;
                accelX -= (accelX > 0 ? 1 : -1) * GameSettings.FRICTION;
                frictionX = false;
            }
            if (Math.abs(velY) >= GameSettings.MAX_VEL) {
                velY = (velY > 0 ? 1 : -1) * GameSettings.MAX_VEL;
            } else if (frictionY && Math.abs(round(velY - accelY)) < Math.abs(accelY)) {
                velY = 0;
                accelY -= (accelY > 0 ? 1 : -1) * GameSettings.FRICTION;
                frictionY = false;
            }
            //apply friction if not currently moving forward
            if (accelX == 0 && velX != 0) {
                frictionX = true;
                accelX += (velX > 0 ? -1 : 1) * GameSettings.FRICTION;
            }
            if (accelY == 0 && velY != 0) {
                frictionY = true;
                accelY += (velY > 0 ? -1 : 1) * GameSettings.FRICTION;
            }

            long endTime = System.nanoTime();
            double execTime = round((endTime - startTime) / 1000000.0);
        }

        /**
         * Method to check if a new position is valid.
         * @param x current x value
         * @param y current y value
         * @param newX new x value
         * @param newY new y value
         * @return Returns whether the movement is valid
         */
        private double[] checkPos(double x, double y, double newX, double newY, double h, double w) {
            if (newX < 0.0 || newX + w > room.getWidth()) {
                return new double[]{(newX < 0 ? 0 : room.getWidth()
                        - w), newY};
            }
            if (newY < 0.0 || newY + h > room.getHeight()) {
                return new double[]{newX, (newY < 0 ? 0 : room.getHeight()
                        - h)};
            }
            return new double[]{newX, newY};
        }

        /**
         * Checks if an object is in the range of the movement vector specified.
         * @param o Object to check for
         * @param x x position
         * @param y y position
         * @param newX new x position
         * @param newY new y position
         * @return Returns whether the object is in range of the player's movement
         */
        private boolean inRange(Obstacle o, double x, double y, double newX, double newY, double h, double w) {
            /* Checks if the object is in range of the player's movement
             *          _________
             *          |[]     |
             *          |  \    |
             *          |   \   |
             *          |    \  |
             *          |     []|
             *          ---------
             */

            if (o.getX() + o.getWidth() < Math.min(x, newX)
                || o.getX() > Math.max(x, newX) + w) {
                return false;
            }
            if (o.getY() + o.getHeight() < Math.min(y, newY)
                || o.getY() > Math.max(y, newY) + h) {
                return false;
            }
            return true;
        }

        /**
         * Checks if the player has entered any doors
         * @param x x position
         * @param y y position
         * @param newX new x position
         * @param newY y position
         * @return Whether the player has entered a door
         */
        private boolean checkDoors(double x, double y, double newX, double newY) {
            Door[] doors = {
                room.getTopDoor(),
                room.getBottomDoor(),
                room.getLeftDoor(),
                room.getRightDoor()
            };

            //loop through doors
            for (Door d : doors) {
                if (d == null) {
                    continue;
                }
                //Check if door is out of player movement vector rectangle
                if (!inRange(d, x, y, newX, newY, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_HEIGHT)) {
                    continue;
                }
                //player movement direction
                boolean moveRight = x < newX;
                boolean moveUp = y < newY;

                //Get equation for intersection
                double[] playerEquation = equation(x, y, newX, newY);
                double[] intersects = getIntersect(d, playerEquation[0], playerEquation[1],
                        moveUp, moveRight, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH);

                //player intersects door
                if (intersects != null) {
                    Room newRoom = d.getGoesTo();
                    Door newDoor;
                    double newStartX;
                    double newStartY;
                    if (d.equals(room.getTopDoor())) {
                        newDoor = newRoom.getBottomDoor();
                        newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                                - GameSettings.PLAYER_WIDTH / 2;
                        newStartY = newDoor.getY() + LayoutGenerator.DOORBOTTOM_HEIGHT + 10;
                    } else if (d.equals(room.getBottomDoor())) {
                        newDoor = newRoom.getTopDoor();
                        newStartX = newDoor.getX() + newDoor.getWidth() / 2.0
                                - GameSettings.PLAYER_WIDTH / 2;
                        newStartY = newDoor.getY() - GameSettings.PLAYER_HEIGHT;
                    } else if (d.equals(room.getRightDoor())) {
                        newDoor = newRoom.getLeftDoor();
                        newStartX = newDoor.getX() + 10 + LayoutGenerator.DOOR_WIDTH;
                        newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                    } else {
                        newDoor = newRoom.getRightDoor();
                        newStartX = newDoor.getX() - 10 - GameSettings.PLAYER_WIDTH;
                        newStartY = newDoor.getY() + newDoor.getHeight() / 5.0;
                    }
                    newRoom.setStartX((int) newStartX);
                    newRoom.setStartY((int) newStartY);
                    setRoom(newRoom);
                    return true;
                }
            }
            return false;
        }

        /**
         * Math method to calculate the intersection point of a line/vector and an object.
         * @param o Object to check
         * @param m Slope of the line
         * @param b Y-intecept of the line
         * @param moveUp Whether the player is moving up
         * @param moveRight Whether the player is moving down
         * @return Returns the x and y coordinate of the intersection point, null if no intersection
         */
        private double[] getIntersect(Obstacle o, double m, double b, boolean moveUp,
                                      boolean moveRight, double h, double w) {
            /* Calculate x-coordinate intersection point on the y-axis
             *
             *          v obstacle
             *          ------------------
             *          |     \          |
             *          |      \         |
             *          --------*---------
             *                  ^\
             *                    \ < movement vector
             *
             * We have equation y = mx + b from playerEquation, which is the vector/line for the
             * player's movement. Since the door is within the rectangle bound by the player's
             * vector as calculated above, we know that any collisions are on the vector and not
             * past it.
             * We pass in the y value of either the top or bottom of the obstacle, depending on if
             * the player is moving downward or upward. If horizontal, it defaults to downward,
             * but it doesn't matter.
             * We then substitute the y in "y = mx + b", along with the m and b values obtained
             * earlier, and solve for x, resulting in an equation "x = (y - b) / m"
             */
            double intY = (o.getY() + o.getHeight() - b) / m;
            if (moveUp) {
                intY = (o.getY() - h - b) / m;
            }
            /* if the player is moving vertically, then slope and y-intercept will be
             * infinity/undefined. If so, set the intY/x position of the intersect to the x
             * coordinate of the movement vector, which will be stored in the y-intercept variable
             */
            if (m == Double.POSITIVE_INFINITY || m == Double.NEGATIVE_INFINITY) {
                intY = b;
            }

            //check if intersect is on the obstacle
            if (intY <= o.getX() + o.getWidth() || intY + w >= o.getX()) {
                double coord = m * intY + b;
                return new double[]{intY, coord};
            }

            //Calculate y-coordinate intersection point on the x-axis, using y = mx + b
            double intX = m * (o.getX() + o.getWidth()) + b;
            if (moveRight) { //intersect is the right side of player to the left side of obstacle
                intX = m * (o.getX() - w) + b;
            }

            //check if intersect is on the obstacle
            if (intX <= o.getY() + o.getHeight() || intX + h >= o.getY()) {
                double coord = (intX - b) / m;
                return new double[]{coord, intX};
            }

            return null;
        }

        /**
         * Moves the player to the specified location.
         * @param x x coordinate to move to
         * @param y y coordinate to move to
         */
        private void movePlayer(double x, double y) {
            //Update player position
            player.getNode().setX(getPx(x));

            //convert game coordinates to JavaFX coordinates
            player.getNode().setY(getPx(room.getHeight() - y - player.getHeight() * 2));

            //Move camera, if needed
            moveCamera();
        }

        private void moveNode(Entity e, double x, double y) {
            ImageView node = e.getNode();
            node.setX(getPx(x));
            node.setY(getPx(room.getHeight() - y - e.getHeight()));
        }

        /**
         * To be implemented.
         */
        private void moveCamera() {
        }

        /**
         * Returns an equation for a specified vector
         * @param x0 first x position
         * @param y0 first y position
         * @param x1 second x position
         * @param y1 second y position
         * @return array of the slope and the y-intercept of the line.
         */
        private double[] equation(double x0, double y0, double x1, double y1) {
            double m = (y1 - y0) / (x1 - x0);
            double b = y0 - m * x0;

            //moving vertically
            if (x0 == x1 && y0 != y1) {
                b = x0;
            }
            return new double[]{m, b};
        }

        private void monsterMove(Monster m) {
            //calculate distance between player and monster
            double mPosY = m.getPosY();
            double mPosX = m.getPosX();
            double ydiff = mPosY - player.getPosY();
            double xdiff = mPosX - player.getPosX();
            double d = round(Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)));
            if (d <= GameSettings.MONSTER_MOVE_RANGE && d >= GameSettings.MONSTER_MOVE_MIN) {
                //move monster towards player
                double angle = Math.atan2(ydiff, xdiff) - Math.PI;
                System.out.println("Player " + player.getPosX() + " " + player.getPosY());
                System.out.println("Angle " + (180 / Math.PI * angle) + " " + ydiff + " " + xdiff);
                double newPosX = mPosX + Math.cos(angle) * m.getSpeed();
                double newPosY = mPosY + Math.sin(angle) * m.getSpeed();

                //check collisions with obstacles
                double[] newPos = checkPos(mPosX, mPosY, newPosX, newPosY,
                        m.getHeight(), m.getWidth());
                int count = 0;
//                System.out.println(d + " Old: " + mPosX + " " + mPosY);
//                while (newPos[0] == mPosX && newPos[1] == mPosY && count < 4) {
//                    angle += Math.PI / 2;
//                    newPosX = mPosX + Math.cos(angle) * m.getSpeed();
//                    newPosY = mPosY + Math.sin(angle) * m.getSpeed();
//                    System.out.println("Check " + count + " a " + angle + " new " + newPosX + " " + newPosY);
//
//                    //check collisions with obstacles
//                    newPos = checkPos(mPosX, mPosY, newPosX, newPosY, m.getHeight(), m.getWidth());
//                    count++;
//                }
                System.out.println(d + " Moving: " + mPosX + ", " + mPosY + " to " + newPos[0] + ", " + newPos[1]);
                if (count >= 4) {
                    System.out.println("Error: Monster stuck.");
                    return;
                }

                //TODO: check collisions with entities

                //move monster
                m.setPosX(newPos[0]);
                m.setPosY(newPos[1]);

                moveNode(m, newPos[0], newPos[1]);

                //check for current attack
                if (d <= GameSettings.MONSTER_ATTACK_RANGE) {
                    if (checkAttack(m)) {
                        //attack player
                        System.out.println("Attacking player.");
                    } else if (m.getReaction() <= 0) {
                        //wind up attack
                        m.setReaction(GameSettings.MONSTER_REACTION_TIME);
                    }
                }
            }
        }

        private boolean checkAttack(Monster m) {
            if (m.getReaction() <= 0) {
                return false;
            }
            double newTime = m.getReaction() - 1000 / GameSettings.FPS;
            //time to attack
            if (newTime <= 0) {
                m.setReaction(0);
                return true;
            } else {
                m.setReaction(newTime);
                return false;
            }
        }
    }
}
