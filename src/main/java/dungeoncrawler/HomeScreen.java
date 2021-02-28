package dungeoncrawler;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class HomeScreen extends GameState {
    private int width;
    private int height;
    private Button startButton;

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
    public Scene getScene() {
        Label label = new Label("Welcome to the Luckless Dungeon Crawler");
        label.getStyleClass().add("title");
        VBox layout = new VBox(label, startButton);
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(10);
        StackPane root = new StackPane(layout);
        Scene scene = new Scene(root, width, height);
        scene.getStylesheets().add("views/HomeScreenStyleSheet.css");
        return scene;
    }

    public Button getStartButton() {
        return startButton;
    }
}