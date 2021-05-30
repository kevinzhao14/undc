package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class SettingsScreen extends GameState {

    SettingsScreen(int width, int height) {
        super(width, height);
        try {
            Parent root = FXMLLoader.load(getClass().getResource("../fxml/SettingsScreen.fxml"));
            scene = new Scene(root, this.width, this.height);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
