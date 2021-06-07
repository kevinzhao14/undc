package undc.gamestates;

import javafx.application.Platform;
import undc.controllers.Controller;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import undc.handlers.Vars;

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
        Button startBtn = new Button("Start");
        startBtn.setId("start-button");

        Button settingsBtn = new Button("Settings");
        settingsBtn.setId("settings-button");

        Button exitBtn = new Button("Exit Game");
        exitBtn.setId("exit-button");

        // Event handling for start, settings, and exit button
        startBtn.setOnAction(event -> Controller.setState(PlayScreen.getInstance()));
        settingsBtn.setOnAction(event -> Controller.setState(SettingsScreen.getInstance()));
        exitBtn.setOnAction(event -> Platform.exit());

        Label label = new Label("Title Here");
        label.getStyleClass().add("title");

        VBox layout = new VBox(label, startBtn, settingsBtn, exitBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);

        StackPane root = new StackPane(layout);
        scene = new Scene(root, width, height);
        scene.getStylesheets().addAll("styles/menu.css", "styles/global.css");
    }

    /**
     * Used to retrieve the singleton instance of HomeScreen.
     * @return the singleton instance of HomeScreen
     */
    public static HomeScreen getInstance() {
        if (instance == null) {
            instance = new HomeScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
        }
        return instance;
    }
}