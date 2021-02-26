package dungeoncrawler;

import javafx.scene.Scene;

public abstract class GameState {
    protected Scene scene;

    public Scene getScene() {
        return scene;
    }
}
