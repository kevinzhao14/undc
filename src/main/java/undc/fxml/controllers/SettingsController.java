package undc.fxml.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.gamestates.HomeScreen;
import undc.handlers.Controls;
import undc.handlers.Vars;

/**
 * Controller for SettingsScreen.fxml
 */
public class SettingsController {
    @FXML
    private ScrollPane video;
    @FXML
    private ScrollPane audio;
    @FXML
    private ScrollPane game;
    @FXML
    private ScrollPane controls;
    @FXML
    private AnchorPane master;
    @FXML
    private TextField upTextField;

    private Button lastButton;
    private ScrollPane lastPane = video;
    private Controls keyControls = Controls.getInstance();

    /**
     * Makes specific settings pane visible.
     * @param childPane The scroll pane we want to make visible.
     * @param button The button pressed to make childPane visible.
     */
    private void show(ScrollPane childPane, Object button) {
        if (master == null) {
            Console.error("Failed to load settings.");
            return;
        }
        if (!(button instanceof Button)) {
            Console.error("Invalid button type.");
            return;
        }
        if (lastPane == childPane) {
            return;
        }
        ((Button) button).getStyleClass().add("button-active");
        if (lastButton != null) {
            lastButton.getStyleClass().remove("button-active");
        }
        if (lastPane != null) {
            lastPane.setVisible(false);
        }
        lastButton = (Button) button;
        childPane.setVisible(true);
        lastPane = childPane;
    }

    public void showVideo(ActionEvent e) {
        show(video, e.getSource());
    }

    public void showAudio(ActionEvent e) {
        show(audio, e.getSource());
    }

    public void showGame(ActionEvent e) {
        show(game, e.getSource());
    }

    public void showControls(ActionEvent e) {
        show(controls, e.getSource());
    }

    /**
     * Changes key binding of input control.
     * @param control Control being changed
     * @param button Button pressed to change key bind
     */
    public void changeKey(String control, Object button) {
        if (!(button instanceof Button)) {
            Console.error("Invalid button type.");
            return;
        }
        Button keyButton = (Button) button;
        keyButton.setText("Press a key");

        Controller.getState().getScene().setOnKeyPressed(event -> {
            String newKeyBind = event.getCode().toString().toLowerCase();
            keyControls.setKey(newKeyBind, control);
            keyButton.setText(newKeyBind);
        });
    }

    public void changeUp(ActionEvent e) {
        changeKey("up", e.getSource());
    }

    public void changeDown(ActionEvent e) {
        changeKey("down", e.getSource());
    }

    public void changeLeft(ActionEvent e) {
        changeKey("left", e.getSource());
    }

    public void changeRight(ActionEvent e) {
        changeKey("right", e.getSource());
    }

    public void changeSprint(ActionEvent e) {
        changeKey("sprint", e.getSource());
    }

    public void back(ActionEvent e) {
        Controller.setState(new HomeScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height")));
    }

    public void reset(ActionEvent e) {
        keyControls.resetKeys();
    }
}
