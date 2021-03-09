package dungeoncrawler;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;

import java.util.Timer;
import java.util.TimerTask;

public class GameController {
    private Timer timer;
    private double velX, velY;
    private double accelX, accelY;
    private boolean isRunning;
    private long ticks;
    private double totalTime;
    private Node player;

    public void start() {

    }

    public void pause() {

    }

    public void resume() {

    }

    public void handleKey(KeyCode key, boolean isPress) {

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
