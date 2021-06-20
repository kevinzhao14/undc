package undc.controllers;

import javafx.application.Platform;
import undc.gamestates.GameState;
import undc.gamestates.HomeScreen;
import javafx.application.Application;
import javafx.stage.Stage;
import undc.handlers.Vars;

/**
 * Class that handles the launching of the game.
 */
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
        stage.widthProperty().addListener((obs, oldVal, newVal) -> Vars.set("gc_screen_width", newVal.intValue() + ""));

        stage.heightProperty().addListener((obs, oldVal, newVal) -> Vars.set("gc_screen_height", newVal.intValue()
                + ""));
        stage.show();

        // catch & print all exceptions to console to manage errors
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            Console.error(e.toString());
            StackTraceElement[] s = e.getStackTrace();
            for (int i = 0; i < 5; i++) {
                if (i >= s.length) {
                    return;
                }
                Console.print(s[i].getClassName() + "." + s[i].getMethodName() + "():" + s[i].getLineNumber());
            }
            Console.print("...and " + (s.length - 5) + " more");
        });
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
