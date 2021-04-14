package dungeoncrawler.controllers;

import dungeoncrawler.gamestates.GameState;
import dungeoncrawler.handlers.Controls;
import dungeoncrawler.handlers.RoomRenderer;
import dungeoncrawler.objects.Bomb;
import dungeoncrawler.objects.Door;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.DroppedItem;
import dungeoncrawler.objects.InventoryItem;
import dungeoncrawler.objects.Item;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Obstacle;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Room;
import dungeoncrawler.objects.Weapon;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;

import java.util.LinkedList;
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
    private boolean isStopped;
    private long ticks;
    private double totalTime;

    //boolean variables for tracking event
    private boolean pressLeft;
    private boolean pressRight;
    private boolean pressUp;
    private boolean pressDown;
    private boolean frictionX;
    private boolean frictionY;
    private boolean isAttacking;

    /**
     * Secondary constructor for no player
     */
    public GameController() {
        this.controls = new Controls();
    }

    /**
     * Starts the game.
     * @param room Current/first room
     */
    public void start(Room room) {
        reset();

        //Render room
        setRoom(room);
        scene = Controller.getState().getScene();

        //Handle key events
        scene.setOnKeyPressed(e -> handleKey(e.getCode().toString(), true));
        scene.setOnKeyReleased(e -> handleKey(e.getCode().toString(), false));
        scene.setOnMousePressed(e -> handleKey(mouseButton(e.getButton()), true));
        scene.setOnMouseReleased(e -> handleKey(mouseButton(e.getButton()), false));
        scene.setOnScroll(e -> handleKey(handleScroll(e.getDeltaY()), false));
    }

    /**
     * Handles mousebutton events and returns the appropriate button name.
     * @param button MouseButton event
     * @return Returns the corresponding button name
     */
    private String mouseButton(MouseButton button) {
        if (button == MouseButton.PRIMARY) {
            return "MOUSE1";
        } else if (button == MouseButton.SECONDARY) {
            return "MOUSE2";
        }
        return "";
    }

    private GameScreen getScreen() {
        GameState state = Controller.getState();
        if (!(state instanceof GameScreen)) {
            throw new IllegalStateException("Illegal Gamestate");
        }
        return (GameScreen) state;
    }

    /**
     * Handle when the player uses the mouse scroll wheel.
     * @param val Scroll length
     * @return String of the keycode
     */
    private String handleScroll(double val) {
        if (val < 0) {
            return "MWHEELDOWN";
        } else if (val > 0) {
            return "MWHEELUP";
        } else {
            return "";
        }
    }

    /**
     * Sets the player object of the game.
     * @param player Player node
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Changes the room.
     * @param newRoom Room to change to
     */
    private void setRoom(Room newRoom) {
        if (isRunning) {
            stop();
        }
        room = newRoom;
        Platform.runLater(() -> getScreen().setRoom(newRoom));
    }

    /**
     * Updates data after room change.
     */
    public void updateRoom() {
        pause();
    }

    /**
     * Resets the player's position to the starting position.
     */
    public void resetPos() {
        player.setPosX(room.getStartX());
        player.setPosY(room.getStartY());
        refresh();
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
        isStopped = false;
        pressLeft = false;
        pressRight = false;
        pressUp = false;
        pressDown = false;
        frictionX = false;
        frictionY = false;
    }

    /**
     * Pauses/resumes the game.
     */
    public void pause() {
        if (isRunning) {
            //System.out.println("Game has been paused");
            //System.out.println("Average FPS in " + ticks + " ticks: " + round(1000.0
            //        / (totalTime / ticks)));
            timer.cancel();
        } else {
            //System.out.println("Game has been resumed");
            startTimer();
        }
        if (!isRunning && isStopped) {
            isStopped = false;
        }
        isRunning = !isRunning;
    }
    public void stop() {
        isRunning = true;
        pause();
        isStopped = true;
        //System.out.println("Game has been stopped.");
    }

    /**
     * Starts the game timer/clock.
     */
    private void startTimer() {
        refresh();
        timer = new Timer();
        timer.schedule(new GameRunner(), 0, 1000 / GameSettings.FPS);
    }

    /**
     * Handler for key events.
     * @param key KeyCode of the key that was pressed
     * @param isPress Whether the event is a press or release event
     */
    private void handleKey(String key, boolean isPress) {
        //Global key binds, regardless of game play/pause state
        if (key.equals(controls.getKey("pause"))) {
            if (!isPress) {
                pause();
                GameScreen screen = getScreen();
                    //esc is used to leave inventory if it's currently open
                    //otherwise, use it to pause/unpause game
                    if (screen.isInventoryVisible()) {
                        screen.toggleInventory();
                    } else {
                        screen.togglePause();
                    }
            }
        }
        //movement keys
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
        }
        if (xval) {
            accelX += sign * GameSettings.ACCEL;
            accelX = round(accelX);
        } else {
            accelY += sign * GameSettings.ACCEL;
            accelY = round(accelY);
        }

        //non-movement keys
        if (key.equals(controls.getKey("attack")) || key.equals(controls.getKey("attack2"))) {
            isAttacking = isPress;
        } else if (key.equals(controls.getKey("inventory"))) {
            if (!isPress) {
                    if (!getScreen().isPaused()) {
                        pause();
                        getScreen().toggleInventory();
                    }
            }
        } else if (key.equals(controls.getKey("use"))) {
            if (isPress == false) {
                return;
            }
            InventoryItem selected = player.getItemSelected();
            System.out.println("Using item " + selected.getItem());
            selected.getItem().use();
        } else if (key.equals(controls.getKey("nextinv"))) {
            player.moveRight();
            getScreen().updateHud();
        } else if (key.equals(controls.getKey("previnv"))) {
            player.moveLeft();
            getScreen().updateHud();
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

    private void refresh() {
        Platform.runLater(() -> RoomRenderer.drawFrame(getScreen().getCanvas(), room, player));
    }

    /**
     * Class that is used to calculate stuff on each tick.
     */
    class GameRunner extends TimerTask {
        /**
         * Primary runner method, controls the data calculations of each tick.
         */
        public void run() {
            if (!(Controller.getState() instanceof GameScreen)) {
                this.cancel();
                return;
            }

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

                //set player sprite
                int dir;
                if (newPosX < posX) {
                    dir = 4;
                } else if (newPosX > posX) {
                    dir = 6;
                } else if (newPosY > posY) {
                    dir = 5;
                } else {
                    dir = 7;
                }
                player.setDirection(dir);

                //check for door intersections
                if (checkDoors(posX, posY, newPosX, newPosY)) {
                    return;
                }
                player.setPosX(newPosX);
                player.setPosY(newPosY);
            }

            //check item pickup
            boolean itemPickedUp = false;
            droploop:
            for (int i = 0; i < room.getDroppedItems().size(); i++) {
                DroppedItem d = room.getDroppedItems().get(i);
                double distX = (player.getPosX() + player.getWidth() / 2) - (d.getX()
                        + d.getWidth() / 2);
                double distY = (player.getPosY() + player.getHeight() / 2) - (d.getY()
                        + d.getHeight() / 2);
                double dist = round(Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2)));
                //pick up item
                if (dist <= GameSettings.PLAYER_PICKUP_RANGE) {
                    //check for item existing in inventory
                    for (InventoryItem[] row : player.getInventory().getItems()) {
                        for (InventoryItem item : row) {
                            if (item == null) {
                                continue;
                            }
                            //if item exists and is not max stack
                            if (item.getItem().equals(d.getItem()) && item.getQuantity()
                                    < item.getItem().getMaxStackSize()) {
                                item.setQuantity(item.getQuantity() + 1);
                                room.getDroppedItems().remove(i);
                                i--;
                                itemPickedUp = true;
                                continue droploop;
                            }
                        }
                    }
                    //not in inventory or inventory items are full
                    if (!player.getInventory().full()) {
                        player.getInventory().add(d.getItem());
                        //remove dropped item
                        room.getDroppedItems().remove(i);
                        i--;
                        itemPickedUp = true;
                    }
                }
            }

            if (itemPickedUp) {
                Platform.runLater(() -> getScreen().updateHud());
            }

            player.setAttackCooldown(Math.max(0.0,
                    player.getAttackCooldown() - 1000.0 / GameSettings.FPS));
            if (isAttacking && player.getAttackCooldown() == 0.0) {
                Item item = player.getItemSelected() != null ? player.getItemSelected().getItem() : null;
                double damage = GameSettings.PLAYER_FIST_DAMAGE;
                double cooldown = GameSettings.PLAYER_FIST_COOLDOWN;
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    damage = weapon.getDamage();
                    cooldown = weapon.getAttackSpeed();
                }
                player.setAttackCooldown(1000 * cooldown);
                for (Monster m : room.getMonsters()) {
                    if (m != null) {
                        double dist = Math.sqrt(Math.pow(player.getPosX() - m.getPosX(), 2)
                                + Math.pow(player.getPosY() - m.getPosY(), 2));
                        if (dist <= GameSettings.PLAYER_ATTACK_RANGE) {
                            boolean slain = m.attackMonster(player.getAttack() * damage);
                            if (slain) {
                                double modifier;
                                switch (Controller.getDataManager().getDifficulty()) {
                                case MEDIUM:
                                    modifier = GameSettings.MODIFIER_MEDIUM;
                                    break;
                                case HARD:
                                    modifier = GameSettings.MODIFIER_HARD;
                                    break;
                                default:
                                    modifier = 1.0;
                                    break;
                                }
                                player.setGold(player.getGold()
                                        + (int) (GameSettings.MONSTER_KILL_GOLD / modifier));
                            }
                        }
                    }
                }
            }

            //Manage Monsters
            for (int i = 0; i < room.getMonsters().length; i++) {
                Monster m = room.getMonsters()[i];
                if (m == null || m.getHealth() == 0) {
                    continue;
                }
                //check and move the monster
                if (m.getHealth() > 0) {
                    if (monsterMove(m)) {
                        return;
                    }
                }
            }

            //manage items (bombs)
            for (int i = 0; i < room.getObstacles().size(); i++) {
                Obstacle o = room.getObstacles().get(i);
                if (o.getItem() == null) {
                    continue;
                }
                //has an item linked
                Item item = o.getItem();
                if (item instanceof Bomb) {
                    Bomb b = (Bomb) item;
                    b.setLivefuse(b.getLivefuse() - 1000 / GameSettings.FPS);
                    //if bomb has blown up
                    if (b.getLivefuse() < 0) {
                        System.out.println("Exploding");
                        double x = o.getX() + o.getHeight() / 2;
                        double y = o.getY() + o.getWidth() / 2;

                        //attack player
                        double distX = Math.pow(x - player.getPosX() + player.getWidth() / 2, 2);
                        double distY = Math.pow(y - player.getPosY() + player.getHeight() / 2, 2);
                        double dist = Math.sqrt(distX + distY);
                        System.out.println("Player d" + dist);
                        if (dist <= b.getRadius()) {
                            System.out.println("Damaging player");
                            player.setHealth(Math.max(0, player.getHealth() - b.getDamage()));
                            System.out.println("New Health " + player.getHealth());
                            Platform.runLater(() -> getScreen().updateHud());
                            if (player.getHealth() == 0) {
                                gameOver(getScreen());
                            }
                        }

                        //get all entities within range of the bomb
                        for (Monster m : room.getMonsters()) {
                            distX = Math.pow(x - m.getPosX() + m.getWidth() / 2, 2);
                            distY = Math.pow(y - m.getPosY() + m.getHealth() / 2, 2);
                            dist = Math.sqrt(distX + distY);
                            if (dist <= b.getRadius()) {
                                m.attackMonster(b.getDamage());
                            }
                        }

                        //remove obstacle
                        room.getObstacles().remove(i);
                        i--;
                    }
                }
            }

            refresh();

            //update velocity
            double ovx = velX;
            double ovy = velY;
            velX += accelX;
            velX = round(velX);
            velY += accelY;
            velY = round(velY);

            //don't allow speed to exceed max
            if (Math.abs(velX) >= GameSettings.MAX_VEL) {
                velX = (velX > 0 ? 1 : -1) * GameSettings.MAX_VEL;
                //was moving before and decelerated to 0
            } else if (frictionX && Math.abs(ovx) <= Math.abs(accelX)) {
                if (velY == 0) {
                    player.setDirection((ovx > 0) ? 2 : 0);
                }
                velX = 0;
                accelX -= (accelX > 0 ? 1 : -1) * GameSettings.FRICTION;
                frictionX = false;
            }
            if (Math.abs(velY) >= GameSettings.MAX_VEL) {
                velY = (velY > 0 ? 1 : -1) * GameSettings.MAX_VEL;
            } else if (frictionY && Math.abs(ovy) <= Math.abs(accelY)) {
                if (velX == 0) {
                    player.setDirection((ovy > 0) ? 1 : 3);
                }
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
            double execTime = round((endTime - startTime) / 1000000.0); //in milliseconds
            totalTime += execTime;
        }

        /**
         * Method to check if a new position is valid.
         * @param x current x value
         * @param y current y value
         * @param newX new x value
         * @param newY new y value
         * @param h height
         * @param w width
         * @return Returns whether the movement is valid
         */
        private double[] checkPos(double x, double y, double newX,
                                  double newY, double h, double w) {
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
         * @param h height
         * @param w width
         * @return Returns whether the object is in range of the player's movement
         */
        private boolean inRange(Obstacle o, double x, double y,
                                double newX, double newY, double h, double w) {
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
                if (!inRange(d, x, y, newX, newY,
                        GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH)) {
                    continue;
                }
                //player movement direction
                boolean moveRight = x < newX;
                boolean moveUp = y < newY;

                //Get equation for intersection
                double[] playerEquation = equation(x, y, newX, newY);
                double[] intersects = getIntersect(d, playerEquation[0], playerEquation[1],
                        moveUp, moveRight, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH);


                //player intersects door, move to new room
                if (intersects != null) {
                    Room newRoom = d.getGoesTo();

                    //check if not visited & if there are still monsters
                    if (!newRoom.wasVisited()) {
                        for (Monster m : room.getMonsters()) {
                            if (m != null && m.getHealth() > 0) {
                                return false;
                            }
                        }
                    }

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
                        newStartY = newDoor.getY() - GameSettings.PLAYER_HEIGHT - 1;
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
         * @param h height
         * @param w width
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

        /**
         * Monster AI for calculating movement and attacking.
         * @param m Monster to calculate for
         */
        private boolean monsterMove(Monster m) {
            //check queue
            LinkedList<double[]> removeList = new LinkedList<>();
            for (double[] e : m.getMoveQueue()) {
                e[0] -= 1000.0 / GameSettings.FPS;
                //time to apply the move
                if (e[0] <= 0) {
                    m.setPosX(e[1]);
                    m.setPosY(e[2]);

                    //remove
                    removeList.add(e);
                }
            }
            for (double[] e : removeList) {
                m.getMoveQueue().remove(e);
            }

            //calculate distance between player and monster
            double[] mq = (m.getMoveQueue().size() > 0)
                    ? m.getMoveQueue().getLast() : new double[]{0, m.getPosX(), m.getPosY()};
            double mPosY = mq[2];
            double mPosX = mq[1];
            double ydiff = (mPosY + m.getHeight() / 2) - (player.getPosY() + player.getHeight() / 2);
            double xdiff = (mPosX + m.getWidth() / 2) - (player.getPosX() + player.getWidth() / 2);
            double d = round(Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)));
            if (d <= GameSettings.MONSTER_MOVE_RANGE && d >= GameSettings.MONSTER_MOVE_MIN) {
                //move monster towards player
                double angle = Math.atan2(ydiff, xdiff) - Math.PI;
                double newPosX = mPosX + round(Math.cos(angle) * m.getSpeed());
                double newPosY = mPosY + round(Math.sin(angle) * m.getSpeed());

                //check collisions with obstacles
                double[] newPos = checkPos(mPosX, mPosY, newPosX, newPosY,
                        m.getHeight(), m.getWidth());
                int count = 0;
                if (count >= 4) {
                    System.out.println("Error: Monster stuck.");
                    return false;
                }

                //add to queue
                double[] moveItem =
                        new double[]{GameSettings.MONSTER_REACTION_TIME, newPos[0], newPos[1]};
                m.getMoveQueue().add(moveItem);
            }
            ydiff = (m.getPosY() + m.getHeight() / 2) - (player.getPosY() + player.getHeight() / 2);
            xdiff = (m.getPosX() + m.getWidth() / 2) - (player.getPosX() + player.getWidth() / 2);
            d = round(Math.sqrt(Math.pow(xdiff, 2) + Math.pow(ydiff, 2)));

            //check for current attack
            if (d <= GameSettings.MONSTER_ATTACK_RANGE) {
                if (checkAttack(m)) {
                    //set attack cooldown
                    m.setAttackCooldown(m.getAttackSpeed() * 1000);
                    //attack player
                    double newHealth = player.getHealth() - m.getAttack();
                    player.setHealth((int) Math.max(0, newHealth));
                    GameScreen screen = getScreen();

                    //use run later to prevent any thread issues
                    Platform.runLater(() -> screen.updateHud());

                    //go to game over screen if player has died
                    if (player.getHealth() == 0.0) {
                        //use run later to prevent any thread issues
                        refresh();
                        gameOver(screen);
                        return true;
                    }
                }
            }
            return false;
        }

        private void gameOver(GameScreen screen) {
            stop();
            Platform.runLater(() -> {
                room = screen.getLayout().getStartingRoom();
                screen.gameOver();
            });
        }

        /**
         * Checks if a monster can attack or not.
         * @param m Monster to check
         * @return Returns if the monster is able to attack
         */
        private boolean checkAttack(Monster m) {
            if (m.getReaction() <= 0 && m.getAttackCooldown() <= 0) {
                m.setReaction(GameSettings.MONSTER_REACTION_TIME);
                return false;
            }
            double newCooldown = m.getAttackCooldown() - 1000.0 / GameSettings.FPS;
            double newTime = m.getReaction() - 1000.0 / GameSettings.FPS;
            m.setAttackCooldown(newCooldown);
            m.setReaction(newTime);

            if (newTime <= 0 && newCooldown <= 0) {
                m.setAttackCooldown(0);
                m.setReaction(0);
                return true;
            } else {
                return false;
            }
        }
    }
}
