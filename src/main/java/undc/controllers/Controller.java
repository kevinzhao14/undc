package undc.controllers;

import javafx.application.Platform;
import undc.gamestates.GameState;
import undc.gamestates.HomeScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import undc.handlers.Vars;

public class Controller extends Application {
    private static Controller instance;

    private Stage stage;
    private GameState state;
    private DataManager dataManager;

    /**
     * Entrypoint for the game.
     *
     * @param stage Stage to use, passed in by JavaFX
     */
    public void start(Stage stage) {
        instance = this;

        //load things
        this.dataManager = new DataManager();
        Vars.load();
        Console.create();

        this.stage = stage;
        state = HomeScreen.getInstance();

        stage.setTitle("UNDC");
        stage.setScene(state.getScene());
        stage.show();
    }

    /**
     * Used to change the GameState, updating the Scene.
     *
     * @param state scene to change to
     */
    public static void setState(GameState state) {
        instance.state = state;
        instance.stage.setScene(state.getScene());
    }

    /**
     * Get the current GameState which is showing on the JavaFX stage.
     *
     * @return current GameState
     */
    public static GameState getState() {
        return instance.state;
    }

    /**
     * Returns the DataManager for the game.
     *
     * @return the DataManager
     */
    public static DataManager getDataManager() {
        return instance.dataManager;
    }

    public static void quit() {
        Platform.exit();
    }
}
