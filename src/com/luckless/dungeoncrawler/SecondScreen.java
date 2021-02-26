package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SecondScreen {
    private int width;
    private int height;
    private Button incrButton;
    private Button decrButton;
    private Label label;

    private SecondScreen() {}
    public SecondScreen(int width, int height) {
        this.width = width;
        this.height = height;
        incrButton = new Button("+");
        decrButton = new Button("-");
        label = new Label("We are in the second screen now!");
    }
    public Scene getScene() {
        label.getStyleClass().add("statusText");
        HBox buttons = new HBox(decrButton, incrButton);
        buttons.getStyleClass().add("buttons");
        VBox vbox = new VBox(label, buttons);
        Scene scene = new Scene(vbox, width, height);
        scene.getStylesheets().add("file:resources/css/SecondScreen.css");
        return scene;
    }

    public Button getIncrButton() {
        return incrButton;
    }

    public Button getDecrButton() {
        return decrButton;
    }

    public void updateLabel(int state) {
        label.setText(Integer.toString(state));
    }
}
