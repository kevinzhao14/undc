package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PlayScreen extends GameState {

    public PlayScreen(int width, int height) {
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
}
