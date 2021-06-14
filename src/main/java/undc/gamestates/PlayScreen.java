package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import undc.handlers.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class PlayScreen extends GameState {
    private static PlayScreen instance;

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
