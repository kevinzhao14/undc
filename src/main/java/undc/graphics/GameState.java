package undc.graphics;

import javafx.scene.Scene;

/**
 * Abstract class that provides a frame work for different game states.
 */
public abstract class GameState {
    protected Scene scene;
    protected int width;
    protected int height;

    public GameState(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Scene getScene() {
        return scene;
    }
}
