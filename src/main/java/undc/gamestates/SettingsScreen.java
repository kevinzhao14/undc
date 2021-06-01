package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

/**
 * Class containing constructor for the settings screen.
 */
public class SettingsScreen extends GameState {

    /**
     * Constructor for SettingsScreen.
     * @param width Width of the screen
     * @param height Height of the screen
     */
    SettingsScreen(int width, int height) {
        super(width, height);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxml/SettingsScreen.fxml"));
            scene = new Scene(root, this.width, this.height);
            scene.getStylesheets().add("styles/global.css");
            scene.getStylesheets().add("styles/settings.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
