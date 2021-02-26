package dungeoncrawler;

import javafx.scene.Scene;

public abstract class GameState {
    protected Scene scene;

    Scene getScene() {
        return scene;
    }
}
