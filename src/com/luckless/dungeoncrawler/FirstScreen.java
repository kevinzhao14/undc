package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class FirstScreen {
    private int width;
    private int height;
    private Button playButton;
    private Button quitButton;

    private FirstScreen() {}
    public FirstScreen(int width, int height) {
        this.width = width;
        this.height = height;
        playButton = new Button("Play");
        quitButton = new Button("Quit");
    }
    public Scene getScene() {
        Label label = new Label("Welcome to the Game!");
        label.getStyleClass().add("statusText");
        HBox buttons = new HBox(quitButton, playButton);
        buttons.getStyleClass().add("buttons");
        VBox vbox = new VBox(label, buttons);
        Scene scene = new Scene(vbox, width, height);
        scene.getStylesheets().add("file:resources/css/FirstScreen.css");
        return scene;
    }

    public Button getQuitButton() {
        return quitButton;
    }

    public Button getPlayButton() {
        return playButton;
    }
}
