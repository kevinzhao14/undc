package dungeoncrawler;

import javafx.application.Application;
import javafx.stage.Stage;

public class Controller extends Application {
    private static Controller INSTANCE;

    private Stage stage;
    private GameState state;
    private DataManager dataManager;

    /**
     * Entrypoint for the game
     *
     * @param stage Stage to use, passed in by JavaFX
     */
    public void start(Stage stage) {
        INSTANCE = this;

        this.dataManager = new DataManager();

        this.stage = stage;
        this.state = new HomeScreen(1280, 720); // placeholder gamestate, this should never be null

        stage.setTitle("Luckless Dungeon Crawler");
        this.stage.setScene(this.state.getScene());

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
     *
     * @return current GameState
     */
    public static GameState getState() {
        return INSTANCE.state;
    }

    /**
     * Returns the DataManager for the game
     *
     * @return the DataManager
     */
    public static DataManager getDataManager() {
        return INSTANCE.dataManager;
    }
}
