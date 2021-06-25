package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import undc.handlers.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class that handles the creation of the play screen.
 */
public class PlayScreen extends GameState {
    private static PlayScreen instance;

    /**
     * Constructor handling the creation of the play screen that is
     * presented when the play wants to load a game or create a new one.
     * @param width int width of the scene
     * @param height int height of the scene
     */
    private PlayScreen(int width, int height) {
        super(width, height);
        try {
            URL url = new File("src/main/java/undc/fxml/PlayScreen.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene = new Scene(root, width, height);
            scene.getStylesheets().addAll("styles/global.css", "styles/playscreen.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retns the current instance of PlayScreen or makes a new one.
     * @return PlayScreen
     */
    public static PlayScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new PlayScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }
}
