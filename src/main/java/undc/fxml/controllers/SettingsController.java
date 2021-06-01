package undc.fxml.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.gamestates.HomeScreen;
import undc.handlers.Controls;
import undc.handlers.Vars;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Controller for SettingsScreen.fxml
 */
public class SettingsController {
    private static ScrollPane video;
    private static ScrollPane audio;
    private static ScrollPane game;
    private static ScrollPane controls;

    @FXML
    private AnchorPane master;
    @FXML
    private Button videoButton;

    private Button lastButton;

    public SettingsController() {
        load();
        Platform.runLater(() -> show(video, videoButton));
    }

    /**
     * Makes specific settings pane visible.
     * @param childPane The scroll pane we want to make visible.
     * @param button The button pressed to make childPane visible.
     */
    private void show(ScrollPane childPane, Object button) {
        if (master == null) {
            Console.error("Failed to load master settings.");
            return;
        }
        if (!(button instanceof Button)) {
            Console.error("Invalid button type.");
            return;
        }

        // set the button as the active button
        if (lastButton != null) {
            lastButton.getStyleClass().remove("button-active");
        }
        lastButton = (Button) button;
        lastButton.getStyleClass().add("button-active");

        // add the new pane to master
        master.getChildren().clear();
        master.getChildren().add(childPane);
        // sets anchor points of new pane so it fills master
        AnchorPane.setTopAnchor(childPane, 0.0);
        AnchorPane.setBottomAnchor(childPane, 0.0);
        AnchorPane.setLeftAnchor(childPane, 0.0);
        AnchorPane.setRightAnchor(childPane, 0.0);

        master.layout();
    }

    private void load() {
        try {
            game = FXMLLoader.load(new File("src/main/java/undc/fxml/GameSettings.fxml").toURI().toURL());
            video = FXMLLoader.load(new File("src/main/java/undc/fxml/VideoSettings.fxml").toURI().toURL());
            audio = FXMLLoader.load(new File("src/main/java/undc/fxml/AudioSettings.fxml").toURI().toURL());
            controls = FXMLLoader.load(new File("src/main/java/undc/fxml/ControlsSettings.fxml").toURI().toURL());
        } catch (IOException e) {
            Console.error("Failed to load settings.");
            e.printStackTrace();
        }
    }

    public void showVideo(ActionEvent e) {
        if (video == null) {
            load();
        }
        show(video, e.getSource());
    }

    public void showAudio(ActionEvent e) {
        if (audio == null) {
            load();
        }
        show(audio, e.getSource());
    }

    public void showGame(ActionEvent e) {
        if (game == null) {
            load();
        }
        show(game, e.getSource());
    }

    public void showControls(ActionEvent e) {
        if (controls == null) {
            load();
        }
        show(controls, e.getSource());
    }

    /**
     * Changes key binding of input control.
     * @param control Control being changed
     * @param button Button pressed to change key bind
     */
    public void changeKey(String control, Object button) {

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
//        keyControls.resetKeys();
    }
}
