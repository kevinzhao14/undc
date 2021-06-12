package undc.fxml.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.gamestates.SettingsScreen;
import undc.handlers.Controls;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * Controller class for controls FXML page.
 */
public class ControlsController extends SettingsPageController {
    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox master;
    @FXML
    private Button cancelButton;
    @FXML
    private GridPane grid;

    private boolean buttonActive = false; // whether or not a button is clicked
    private Button activeButton; // currently active button

    /**
     * Constructor for controls settings UI. Delays the runtime to properly display the page.
     */
    public ControlsController() {
        Platform.runLater(this::load);
    }

    /**
     * Fills buttons with current key mapping for each control.
     */
    private void load() {
        HashMap<String, String> temp = new HashMap<>();
        Controls.getInstance().getMapUnmodifiable().forEach((k, v) -> temp.put(v, k.toUpperCase()));

        // go through each button to load its respective control key
        Set<Node> buttons = master.lookupAll(".controls-grid Button");
        for (Node n : buttons) {
            if (!(n instanceof Button)) {
                Console.error("Invalid node");
                return;
            }
            if (n.getId() != null) {
                Button b = (Button) n;
                b.setText(temp.get(n.getId()));
                b.setOnMouseReleased(e -> changeKey(e, b));
            }
        }
    }

    /**
     * Adjusts scrolling speed to faster level.
     * @param e ScrollEvent to trigger ActionEvent on scroll wheel scroll
     */
    public void scroll(ScrollEvent e) {
        scroller.setVvalue(scroller.getVvalue() - e.getDeltaY() * 0.003);
    }

    /**
     * Changes a control to a pressed key by calling handleChangeKey if proper conditions are met.
     * @param button ActionEvent triggered on key press, mouse click, or scroll wheel
     */
    public void changeKey(MouseEvent me, Button button) {
        if (buttonActive || me.getButton() != MouseButton.PRIMARY) {
            return;
        }
        buttonActive = true;

        // Setting up values for functionality of cancel method and cancel button
        cancelButton.setVisible(true);
        activeButton = button;

        button.setText("Press a key");

        Scene scene = Controller.getState().getScene();

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                handleChangeKey(button, "cancel");
            } else {
                handleChangeKey(button, e.getCode().toString());
            }
        });
        scene.setOnMousePressed(e -> handleChangeKey(button, Controls.mbStringify(e.getButton())));
        grid.setOnScroll(e -> {
            handleChangeKey(button, Controls.scrollStringify(e.getDeltaY()));
            e.consume();
        });
        // Allows mouse controls to be set by clicking on the button
        button.setOnMouseReleased(e -> handleChangeKey(button, Controls.mbStringify(e.getButton())));
    }

    /**
     * Changes the key bind to the passed in key.
     * @param button Button containing the control being changed
     * @param key String for new key bind
     */
    private void handleChangeKey(Button button, String key) {
        String control = button.getId();
        Controls c = Controls.getInstance();
        if (!key.equalsIgnoreCase("cancel") && !key.equalsIgnoreCase(c.getKey(control))) {
            //unbind old key
            String oldKey = c.getKey(control);
            c.removeKey(oldKey);

            //fix current bind
            String oldControl = c.getControl(key);
            Node oldButton = master.lookup("#" + oldControl);
            if (oldButton != master) { //lookup returns master/root node if no results found
                if (!(oldButton instanceof Button)) {
                    Console.error("Could not unbind duplicate key!");
                    button.setText("");
                    resetHandlers();
                    return;
                } else {
                    ((Button) oldButton).setText("");
                }
            }
            c.setKey(key, control);
            button.setText(key.toUpperCase());
        } else {
            button.setText(c.getKey(control));
        }
        resetHandlers();
        buttonActive = false;
        cancelButton.setVisible(false);
        button.setOnMouseReleased(e -> changeKey(e, button));
        grid.setOnScroll(this::scroll);
    }

    /**
     * Resets controls to default values.
     */
    private void resetHandlers() {
        Scene scene = Controller.getState().getScene();
        scene.setOnKeyPressed(e -> {});
        scene.setOnMouseClicked(e -> {});
        scene.setOnScroll(e -> {});
    }

    /**
     * Calls resetHandlers and reloads page.
     */
    public void reset() {
        SettingsScreen.getInstance().showPopup(this);
    }

    /**
     * Resets the most recently changed control button or the control button that is currently being changed.
     */
    public void cancel() {
        handleChangeKey(activeButton, Controls.getInstance().getKey(activeButton.getId()));
    }

    public void resetSettings() {
        resetHandlers();
        Controls.getInstance().resetKeys();
        load();
    }
}
