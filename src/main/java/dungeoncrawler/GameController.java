package dungeoncrawler;

import javafx.scene.image.ImageView;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

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

    public GameController(Room room, Scene scene, ImageView player) {
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
        this.room = room;
        this.scene = scene;
        this.player = player;
        this.timer = new Timer();
        this.controls = new Controls();
    }

    public void start() {
        //Reset the game
        reset();

        //Handle key events
        scene.setOnKeyPressed(e -> {
            handleKey(e.getCode(), true);
        });
        scene.setOnKeyPressed(e -> {
            handleKey(e.getCode(), false);
        });
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
                if (atDoor(posX, posY, newPosX, newPosY)) {

                }
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
            if (o.getX() + o.getWidth() < Math.min(x, newX)
                && o.getX() > Math.max(x, newX) + GameSettings.PLAYER_WIDTH) {
                return false;
            }
            if (o.getY() + o.getHeight() < Math.min(y, newY)
                && o.getY() > Math.max(y, newY) + GameSettings.PLAYER_HEIGHT) {
                return false;
            }
            return true;
        }

        //TODO insert comments below in javadoc
        //intX -> point of intersection in x-axis
        //intY -> point of intersection in y-axis
        private boolean checkObstacle(Obstacle o, double intX, double intY,
                                      boolean left, boolean bottom) {
            return true;
        }

        private boolean atDoor(double x, double y, double newX, double newY) {
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
                //Get equation for intersection
                double[]
                //Check if player is inside door

                if (checkObstacle(d, ))
            }
        }

        private void movePlayer(double x, double y) {
            //Update player position
            player.setX(getPx(posX));
            player.setY(scene.getHeight() - getPx(posY));

            //Move camera, if needed
            moveCamera();
        }

        //Implement if necessary
        private void moveCamera() {
        }

        /**
         *
         * @param m slope of equation 1
         * @param b y-intercept of equation 1
         * @param a slope of equation 2
         * @param c y-intercept of equation 2
         * @return x and y coordinates of intersection
         */
        private double[] intersect(double m, double b, double a, double c) {
            double x = (b - c) / (a - m);
            double y = m * x + b;
            return new double[]{x, y};
        }

        private double[] equation(double x0, double y0, double x1, double y1) {
            double m = (y1 - y0) / (x1 - x0);
            double b = y0 - m * x0;
            return new double[]{m, b};
        }
    }
}
