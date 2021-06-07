package undc.fxml.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.handlers.Controls;

import java.util.HashMap;
import java.util.Set;

/**
 * Controller class for controls FXML page.
 */
public class ControlsController {
    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox master;
    @FXML
    private Button cancelButton;

    private boolean buttonActive = false; // whether or not a button is clicked
    private int clicks = 0;
    private Button activeButton; // currently active button
    private String currentKey;

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
                ((Button) n).setText(temp.get(n.getId()));
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
     * @param ae ActionEvent triggered on key press, mouse click, or scroll wheel
     */
    public void changeKey(ActionEvent ae) {
        if (buttonActive) {
            return;
        }
        buttonActive = true;
        if (!(ae.getSource() instanceof Button)) {
            Console.error("Invalid button type.");
            return;
        }
        Button button = (Button) ae.getSource();

        // Setting up values for functionality of cancel method and cancel button
        cancelButton.setVisible(true);
        activeButton = button;
        currentKey = activeButton.getText();

        button.setText("Press a key");

        Scene scene = Controller.getState().getScene();


        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                handleChangeKey(button, "cancel");
            } else {
                handleChangeKey(button, e.getCode().toString());
            }
        });
        scene.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                clicks = (clicks + 1) % 2;
            }
            handleChangeKey(button, Controls.mbStringify(e.getButton()));
        });
        scene.setOnScroll(e -> handleChangeKey(button, Controls.scrollStringify(e.getDeltaY())));
        // Allows mouse controls to be set by clicking on the button
        button.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                handleChangeKey(button, Controls.mbStringify(e.getButton()));
            } else if (button.getText().equals("Press a key")) {
                clicks = (clicks + 1) % 2;
                if (clicks == 0) {
                    handleChangeKey(button, Controls.mbStringify(e.getButton()));
                }
            }
        });
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
        clicks = 0;

        cancelButton.setVisible(false);
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
        resetHandlers();
        Controls.getInstance().resetKeys();
        load();
    }

    /**
     * Resets the most recently changed control button or the control button that is currently being changed.
     */
    public void cancel() {
        handleChangeKey(activeButton, currentKey);
    }
}
