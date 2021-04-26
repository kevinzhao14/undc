package dungeoncrawler.gamestates;

import dungeoncrawler.controllers.Controller;
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
    private int width;
    private int height;
    private Button startButton;

    /**
     * HomeScreen constructor which also sets the start button functionality
     * @param width the width of the window
     * @param height the height of the window
     */
    public HomeScreen(int width, int height) {
        this.width = width;
        this.height = height;
        startButton = new Button("Start");
        startButton.setId("start-button");
        //dummy code
        startButton.setOnAction(event -> {
            InitPlayerConfigScreen config = new InitPlayerConfigScreen(width, height);
            Controller.setState(config);
        });
    }

    /**
     * creates the home screen scene
     * @return the home screen scene
     */
    public Scene getScene() {
        Label label = new Label("Welcome to the Luckless Dungeon Crawler");
        label.getStyleClass().add("title");
        VBox layout = new VBox(label, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().addAll("styles/HomeScreenStyleSheet.css",
                "http://fonts.googleapis.com/css?family=VT323");
        return scene;
    }

    /**
     * @return the start button
     */
    public Button getStartButton() {
        return startButton;
    }
}