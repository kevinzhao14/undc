package undc.graphics.fxml.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import undc.command.Console;
import undc.command.DataManager;
import undc.command.Vars;
import undc.general.Audio;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Class that controls the Audio FXML file.
 */
public class AudioController implements Initializable {

    @FXML
    private VBox master;

    private int percent;
    private Set<Node> sliders;
    private Set<Node> buttons;
    private boolean buttonActive = false; // whether or not a button is clicked
    private Button activeButton; // currently active button

    /**
     * Sets up a listener for the sliders to show the percentage.
     * @param arg0 URL
     * @param arg1 ResourceBundle
     */
    public void initialize(URL arg0, ResourceBundle arg1) {
        sliders = master.lookupAll(".controls-grid Slider");
        buttons = master.lookupAll(".control-grid Button");
        for (Node n : sliders) {
            if (!(n instanceof Slider)) {
                Console.error("invalid node");
                return;
            }
            Slider slider = (Slider) n;
            Button button = (Button) slider.getParent().getChildrenUnmodifiable().get(1);
            // Set up initial volumes
            slider.setValue(Vars.d("volume") * 100);
            button.setText((int) slider.getValue() + "%");

            // Add listener to apply slider value to button as percent
            slider.valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    percent = (int) slider.getValue();
                    button.setText(percent + "%");

                    // Change volume to new value of slider
                    if (slider.getId().equals("master-volume")) {
                        for (Audio audio : DataManager.SOUNDS.values()) {
                            audio.getClip().setVolume(slider.getValue() / 100);
                        }
                    }
                }
            });
            for (Node node : buttons) {
                if (!(node instanceof Button)) {
                    Console.error("Invalid node");
                    return;
                }
                Button b = (Button) node;
                b.setOnMouseReleased(e -> changeVolume(e, b));
            }
        }
    }

    public void changeVolume(MouseEvent me, Button button) {
        // ToDo
    }
}
