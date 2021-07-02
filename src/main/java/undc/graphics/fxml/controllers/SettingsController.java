package undc.graphics.fxml.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import undc.command.Console;
import undc.general.Controller;
import undc.graphics.HomeScreen;

import java.io.File;
import java.io.IOException;

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

    /**
     * Default constructor that is called to load the settings page.
     */
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

    /**
     * Creates the panes for the various setting UIs.
     */
    private void load() {
        try {
            game = FXMLLoader.load(new File("src/main/java/undc/graphics/fxml/GameSettings.fxml").toURI().toURL());
            video = FXMLLoader.load(new File("src/main/java/undc/graphics/fxml/VideoSettings.fxml").toURI().toURL());
            audio = FXMLLoader.load(new File("src/main/java/undc/graphics/fxml/AudioSettings.fxml").toURI().toURL());
            controls = FXMLLoader.load(
                    new File("src/main/java/undc/graphics/fxml/ControlsSettings.fxml").toURI().toURL());
        } catch (IOException e) {
            Console.error("Failed to load settings.");
            e.printStackTrace();
        }
    }

    /**
     * Makes the video settings UI visible to the player.
     * @param e ActionEvent to trigger method upon button click
     */
    public void showVideo(ActionEvent e) {
        if (video == null) {
            load();
        }
        show(video, e.getSource());
    }

    /**
     * Makes the audio settings UI visible to the player.
     * @param e ActionEvent to trigger method upon button click
     */
    public void showAudio(ActionEvent e) {
        if (audio == null) {
            load();
        }
        show(audio, e.getSource());
    }

    /**
     * Makes the game settings UI visible to the player.
     * @param e ActionEvent to trigger method upon button click
     */
    public void showGame(ActionEvent e) {
        if (game == null) {
            load();
        }
        show(game, e.getSource());
    }

    /**
     * Makes the game settings UI visible to the player.
     * @param e ActionEvent to trigger method upon button click
     */
    public void showControls(ActionEvent e) {
        if (controls == null) {
            load();
        }
        show(controls, e.getSource());
    }

    public void back() {
        HomeScreen.resetInstance();
        Controller.setState(HomeScreen.getInstance());
    }
}
