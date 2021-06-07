package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import undc.handlers.Vars;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents the setting screen. The settings screen is used by the player to modify in-game controls.
 */
public class SettingsScreen extends GameState {
    private static SettingsScreen instance;

    /**
     * Constructor for SettingsScreen.
     * @param width Width of the screen
     * @param height Height of the screen
     */
    private SettingsScreen(int width, int height) {
        super(width, height);
        try {
            URL url = new File("src/main/java/undc/fxml/SettingsScreen.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene = new Scene(root, this.width, this.height);
            scene.getStylesheets().add("styles/global.css");
            scene.getStylesheets().add("styles/settings.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to retrieve the singleton instance of SettingsScreen.
     * @return the singleton instance of SettingsScreen
     */
    public static SettingsScreen getInstance() {
        if (instance == null) {
            instance = new SettingsScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
        }
        return instance;
    }
}
