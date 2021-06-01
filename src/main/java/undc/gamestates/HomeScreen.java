package undc.gamestates;

import javafx.application.Platform;
import undc.controllers.Controller;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * HomeScreen Page for the Dungeon Crawler
 */
public class HomeScreen extends GameState {
    private Button startBtn;
    private Button settingsBtn;
    private Button exitBtn;

    /**
     * HomeScreen constructor which also sets the start, settings, and exit button functionality
     *
     * @param width  the width of the window
     * @param height the height of the window
     */
    public HomeScreen(int width, int height) {
        super(width, height);
        startBtn = new Button("Start");
        startBtn.setId("start-button");

        settingsBtn = new Button("Settings");
        settingsBtn.setId("settings-button");

        exitBtn = new Button("Exit Game");
        exitBtn.setId("exit-button");


        //Event handling for start, settings, and exit button
        startBtn.setOnAction(event -> {
            PlayScreen playScreen = new PlayScreen(this.width, this.height);
//            ConfigScreen playScreen = new ConfigScreen(this.width, this.height);
            Controller.setState(playScreen);
        });
        settingsBtn.setOnAction(event -> {
            Controller.setState(new SettingsScreen(this.width, this.height));
        });
        exitBtn.setOnAction(event -> {
            Platform.exit();
        });
    }

    /**
     * creates the home screen scene
     *
     * @return the home screen scene
     */
    public Scene getScene() {
        Label label = new Label("Title Here");
        label.getStyleClass().add("title");

        VBox layout = new VBox(label, startBtn, settingsBtn, exitBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);

        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().addAll("styles/menu.css", "styles/global.css");
        return scene;
    }

    /**
     * @return the start button
     */
    public Button getStartBtn() {
        return startBtn;
    }
}