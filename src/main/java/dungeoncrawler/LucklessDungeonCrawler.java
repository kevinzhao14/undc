package dungeoncrawler;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class LucklessDungeonCrawler extends Application {
    private static LucklessDungeonCrawler INSTANCE;

    private Stage stage;
    private GameState state;

    public void start(Stage stage) {
        INSTANCE = this;

        this.stage = stage;
        this.state = null; // placeholder gamestate, this should never be null

        stage.setTitle("Luckless Dungeon Crawler");
        this.stage.setScene(new Scene(new Pane())); // placeholder scene

        stage.show();
    }

    /**
     * Used to change the GameState, updating the Scene.
     *
     * @param state scene to change to
     */
    public static void setState(GameState state) {
        INSTANCE.state = state;
        INSTANCE.stage.setScene(state.getScene());
    }

    /**
     * Get the current GameState which is showing on the JavaFX stage.
     */
    public static GameState getState() {
        return INSTANCE.state;
    }
}
