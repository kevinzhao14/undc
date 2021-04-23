package dungeoncrawler.gamestates;

import javafx.scene.Scene;

public abstract class GameState {
    protected Scene scene;
    protected int sceneWidth;
    protected int sceneHeight;

    public Scene getScene() {
        return scene;
    }
}
