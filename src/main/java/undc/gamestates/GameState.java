package undc.gamestates;

import javafx.scene.Scene;

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
