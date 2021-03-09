package dungeoncrawler;

import javafx.scene.Node;
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
    private Node player;
    private double posX, posY;
    private double velX, velY;
    private double accelX, accelY;
    private boolean isRunning;
    private long ticks;
    private double totalTime;

    public GameController(Room room, Scene scene, Node player) {
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

    class GameRunner extends TimerTask {
        public void run() {

        }

        private boolean checkPos(double x, double y, double newX, double newY) {

            return false;
        }

        private void movePlayer(double x, double y) {

        }
    }
}
