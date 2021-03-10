package dungeoncrawler;

import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private Timer timer;

    //TODO: Create Controls object in Controller
    private Controls controls;
    private Room room;
    private Scene scene;
    private ImageView player;
    private double posX, posY;
    private double velX, velY;
    private double accelX, accelY;
    private boolean isRunning;
    private long ticks;
    private double totalTime;

    public GameController(ImageView player) {
        if (room == null) {
            throw new IllegalArgumentException(
                    "Cannot assign null Room reference to GameController instance"
            );
        }
        if (scene == null) {
            throw new IllegalArgumentException(
                    "Cannot assign null Scene reference to GameController instance"
            );
        }
        if (player == null) {
            throw new IllegalArgumentException(
                    "Cannot assign null Player reference to GameController instance"
            );
        }
        this.player = player;
        this.timer = new Timer();
        this.controls = new Controls();
    }

    public void start(Room room) {
        //Render room
        setRoom(room);

        //Handle key events
        scene.setOnKeyPressed(e -> {
            handleKey(e.getCode(), true);
        });
        scene.setOnKeyPressed(e -> {
            handleKey(e.getCode(), false);
        });
    }

    private void setRoom(Room newRoom) {
        room = newRoom;
        scene = RoomRenderer.drawRoom(newRoom, player);
        reset();
        isRunning = true;
        Controller.setScene(scene);
    }

    private void reset() {
        posX = room.getStartX();
        posY = room.getStartY();
        velX = 0.0;
        velY = 0.0;
        accelX = 0.0;
        accelY = 0.0;
        isRunning = false;
        ticks = 0;
        totalTime = 0;
    }

    public void pause() {
        if (isRunning) {
            timer.cancel();
        } else {
            startTimer();
        }

        isRunning = !isRunning;
    }

    private void startTimer() {
        timer.schedule(new GameRunner(), 0, 1000 / GameSettings.FPS);
    }

    private void handleKey(KeyCode keyCode, boolean isPress) {
        if (!isRunning) {
            return;
        }
        String key = keyCode.toString();

        if (key.equals(controls.getKey("up"))) {
            accelY += (isPress && accelY < GameSettings.ACCEL ? 1 : -1) * GameSettings.ACCEL;
            accelY = round(accelY);
        } else if (key.equals(controls.getKey("down"))) {
            accelY += (isPress && accelY > -GameSettings.ACCEL ? -1 : 1) * GameSettings.ACCEL;
            accelY = round(accelY);
        } else if (key.equals(controls.getKey("right"))) {
            accelX += (isPress && accelX < GameSettings.ACCEL ? 1 : -1) * GameSettings.ACCEL;
            accelX = round(accelX);
        } else if (key.equals(controls.getKey("left"))) {
            accelX += (isPress && accelX > -GameSettings.ACCEL ? -1 : 1) * GameSettings.ACCEL;
            accelX = round(accelX);
        }
    }

    private double round(double number) {
        return Math.round(number * GameSettings.PRECISION) / GameSettings.PRECISION;
    }

    private double getPx(double units) {
        return units * GameSettings.PPU;
    }

    class GameRunner extends TimerTask {
        public void run() {
            ticks++;
            double newPosX = posX + velX;
            double newPosY = posY + velY;

            boolean isValidPos = checkPos(posX, posY, newPosX, newPosY);
            if (isValidPos) {
                movePlayer(newPosX, newPosY);
                checkDoors(posX, posY, newPosX, newPosY);
                posX = newPosX;
                posY = newPosY;
            }
        }


        private boolean checkPos(double x, double y, double newX, double newY) {
            if (newX < 0.0 || newX > room.getWidth()) {
                return false;
            }
            if (newY < 0.0 || newY > room.getHeight()) {
                return false;
            }

            return true;
        }

        private boolean inRange(Obstacle o, double x, double y, double newX, double newY) {
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
                || o.getX() > Math.max(x, newX) + GameSettings.PLAYER_WIDTH) {
                return false;
            }
            if (o.getY() + o.getHeight() < Math.min(y, newY)
                || o.getY() > Math.max(y, newY) + GameSettings.PLAYER_HEIGHT) {
                return false;
            }
            return true;
        }

        private void checkDoors(double x, double y, double newX, double newY) {
            //TODO: implement getDoors() method in Room class
            Door[] doors = {
                room.getTopDoor(),
                room.getBottomDoor(),
                room.getLeftDoor(),
                room.getRightDoor()
            };


            for (Door d : doors) {
                //Check if door is out of player movement vector rectangle
                if (!inRange(d, x, y, newX, newY)) {
                    continue;
                }
                //player movement direction
                boolean moveRight = x < newX;
                boolean moveUp = y < newY;

                //Get equation for intersection
                double[] playerEquation = equation(x, y, newX, newY);
                double[] intersects = getIntersect(d, playerEquation[0], playerEquation[1], moveUp, moveRight);

                //player intersects door
                if (intersects != null) {
                    Room newRoom = d.getGoesTo();
                    setRoom(newRoom);
                }
            }
        }

        private double[] getIntersect(Obstacle o, double m, double b, boolean moveUp, boolean moveRight) {
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
             * We have equation y = mx + b from playerEquation, which is the vector/line for the player's movement.
             * Since the door is within the rectangle bound by the player's vector as calculated above, we know that
             * any collisions are on the vector and not past it.
             * We pass in the y value of either the top or bottom of the obstacle, depending on if the player is
             * moving downward or upward. If horizontal, it defaults to downward, but it doesn't matter.
             * We then substitute the y in "y = mx + b", along with the m and b values obtained earlier, and solve
             * for x, resulting in an equation "x = (y - b) / m"
             */
            double intY = (o.getY() + o.getHeight() - b) / m;
            if (moveUp) {
                intY = (o.getY() - GameSettings.PLAYER_HEIGHT - b) / m;
            }

            //check if intersect is on the obstacle
            if (intY <= o.getX() + o.getWidth() || intY + GameSettings.PLAYER_WIDTH >= o.getX()) {
                double coord = m * intY + b;
                return new double[]{intY, coord};
            }

            //Calculate y-coordinate intersection point on the x-axis, using y = mx + b
            double intX = m * (o.getX() + o.getWidth()) + b;
            if (moveRight) { //intersect is the right side of the player to the left side of the obstacle
                intX = m * (o.getX() - GameSettings.PLAYER_WIDTH) + b;
            }

            //check if intersect is on the obstacle
            if (intX <= o.getY() + o.getHeight() || intX + GameSettings.PLAYER_HEIGHT >= o.getY()) {
                double coord = (intX - b) / m;
                return new double[]{coord, intX};
            }

            return null;
        }

        private void movePlayer(double x, double y) {
            //Update player position
            player.setX(getPx(posX));
            player.setY(scene.getHeight() - getPx(posY) - GameSettings.PLAYER_HEIGHT);

            //Move camera, if needed
            moveCamera();
        }

        //Implement if necessary
        private void moveCamera() {
        }

        private double[] equation(double x0, double y0, double x1, double y1) {
            double m = (y1 - y0) / (x1 - x0);
            double b = y0 - m * x0;
            return new double[]{m, b};
        }
    }
}
