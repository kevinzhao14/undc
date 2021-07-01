package undc.graphics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import undc.command.Console;
import javafx.scene.Scene;
import undc.command.Vars;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * HomeScreen Page for the Dungeon Crawler.
 */
public class HomeScreen extends GameState {
    private static HomeScreen instance;


    /**
     * HomeScreen constructor which also sets the start, settings, and exit button functionality.
     * @param width  the width of the window
     * @param height the height of the window
     */
    private HomeScreen(int width, int height) {
        super(width, height);
        Parent root;
        try {
            URL url = new File("src/main/java/undc/fxml/HomeScreen.fxml").toURI().toURL();
            root = FXMLLoader.load(url);
        } catch (IOException e) {
            Console.error("Failed to load home screen.");
            return;
        }
        scene = new Scene(root, this.width, this.height);
        scene.getStylesheets().add("styles/global.css");
        scene.getStylesheets().add("styles/home.css");
    }

    /**
     * Used to retrieve the singleton instance of HomeScreen.
     * @return the singleton instance of HomeScreen
     */
    public static HomeScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new HomeScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }
}