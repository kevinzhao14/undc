package undc.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import undc.command.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Class that handles the creation of the new game screen.
 */
public class NewGameScreen extends GameState {
    private static NewGameScreen instance;

    /**
     * Constructor for a NewGameScreen that is presented when a player wants to start playing a new game.
     * @param width int width of the scene
     * @param height int height of the scene
     */
    public NewGameScreen(int width, int height) {
        super(width, height);
        try {
            URL url = new File("src/main/java/undc/graphics/fxml/NewGameScreen.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene = new Scene(root, width, height);
            scene.getStylesheets().addAll("styles/global.css", "styles/newgame.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the current instance of the GameScreen.
     * @return NewGameScreen
     */
    public static NewGameScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new NewGameScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }
}
