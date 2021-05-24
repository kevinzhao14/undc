package undc.gamestates;

import javafx.stage.Stage;
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
     * @param width the width of the window
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
            ConfigScreen config = new ConfigScreen(this.width, this.height);
            Controller.setState(config);
        });
        settingsBtn.setOnAction(event -> {
            //System.out.println("Settings button clicked");
            Controller.setState(new SettingsScreen(800  , 542));
        });
        exitBtn.setOnAction(event -> {
            // System.out.println("Exit button clicked");
            exitGame(exitBtn);
        });
    }

    /**
     * creates the home screen scene
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

    /**
     * Closes the game, should be called from handle method.
     * @param btn The button that triggers the game closing
     */
    public void exitGame(Button btn) {
        ((Stage)(btn.getScene().getWindow())).close();
    }
}