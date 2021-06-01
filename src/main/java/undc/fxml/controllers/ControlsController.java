package undc.fxml.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.VBox;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.handlers.Controls;

import java.util.HashMap;
import java.util.Set;

public class ControlsController {
    @FXML
    private ScrollPane scroller;
    @FXML
    private VBox master;

    public ControlsController() {
        Platform.runLater(this::load);
    }

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

    public void scroll(ScrollEvent e) {
        scroller.setVvalue(scroller.getVvalue() - e.getDeltaY() * 0.003);
    }

    public void changeKey(ActionEvent ae) {
        if (!(ae.getSource() instanceof Button)) {
            Console.error("Invalid button type.");
            return;
        }
        Button button = (Button) ae.getSource();

        button.setText("Press a key");

        Scene scene = Controller.getState().getScene();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                handleChangeKey(button, "cancel");
            } else {
                handleChangeKey(button, e.getCode().toString());
            }
        });
        scene.setOnMouseClicked(e -> handleChangeKey(button, Controls.mbStringify(e.getButton())));
        scene.setOnScroll(e -> handleChangeKey(button, Controls.scrollStringify(e.getDeltaY())));
    }

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
    }

    private void resetHandlers() {
        Scene scene = Controller.getState().getScene();
        scene.setOnKeyPressed(e -> {});
        scene.setOnMouseClicked(e -> {});
        scene.setOnScroll(e -> {});
    }

    public void reset() {
        resetHandlers();
        Controls.getInstance().resetKeys();
        load();
    }
}
