package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import undc.handlers.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class NewGameScreen extends GameState {
    private static NewGameScreen instance;

    public NewGameScreen(int width, int height) {
        super(width, height);
        try {
            URL url = new File("src/main/java/undc/fxml/NewGameScreen.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);
            scene = new Scene(root, width, height);
            scene.getStylesheets().addAll("styles/global.css", "styles/newgame.css");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
