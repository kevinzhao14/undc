package undc.general;

import javafx.application.Platform;
import javafx.scene.input.KeyCombination;
import undc.command.Console;
import undc.command.DataManager;
import undc.command.Vars;
import undc.graphics.GameState;
import undc.graphics.HomeScreen;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Class that handles the launching of the game.
 */
public class Controller extends Application {
    private static Controller instance;

    private Stage stage;
    private GameState state;

    /**
     * Entrypoint for the game.
     *
     * @param stage Stage to use, passed in by JavaFX
     */
    public void start(Stage stage) {
        instance = this;
        this.stage = stage;

        // load things
        Vars.load();
        DataManager.getInstance();
        Console.create();
        Config.getInstance();

        setState(HomeScreen.getInstance());

        stage.setTitle("UNDC");
        stage.setFullScreenExitHint("");
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
            e.printStackTrace();
        });
    }

    public static GameState getState() {
        return instance.state;
    }

    /**
     * Used to change the GameState, updating the Scene.
     *
     * @param state scene to change to
     */
    public static void setState(GameState state) {
        instance.state = state;
        instance.stage.setScene(state.getScene());
        setFullscreen();
    }

    public static void quit() {
        Platform.exit();
    }

    public static Controller getInstance() {
        return instance;
    }

    public static void setFullscreen() {
        instance.stage.setFullScreen(Vars.b("gc_fullscreen"));
        instance.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }
}
