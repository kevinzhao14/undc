package undc.graphics.fxml.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import undc.command.Console;
import undc.command.Vars;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Class that controls the Audio FXML file.
 */
public class AudioController implements Initializable {

    @FXML
    private VBox master;

    /**
     * Sets up a listener for the sliders to show the percentage.
     * @param arg0 URL
     * @param arg1 ResourceBundle
     */
    public void initialize(URL arg0, ResourceBundle arg1) {
        Set<Node> sliders = master.lookupAll(".controls-grid Slider");
        Set<Node> buttons = master.lookupAll(".control-grid Button");
        for (Node n : sliders) {
            if (!(n instanceof Slider)) {
                Console.error("Invalid node.");
                return;
            }
            Slider slider = (Slider) n;
            Button button = (Button) slider.getParent().getChildrenUnmodifiable().get(1);
            // Set up initial volumes
            String id = slider.getId();
            switch (id) {
                case "master-volume":
                    slider.setValue(Vars.d("volume"));
                    break;
                case "effects-volume":
                    slider.setValue(Vars.d("cl_effects_volume"));
                    break;
                case "music-volume":
                    slider.setValue(Vars.d("cl_music_volume"));
                    break;
            }
            button.setText(Math.round(slider.getValue() * 100) + "%");

            // Add listener to apply slider value to button as percent
            slider.valueProperty().addListener((arg01, arg11, arg2) -> {
                button.setText(Math.round(slider.getValue() * 100) + "%");
                // Change volume to new value of slider
                switch (id) {
                    case "master-volume":
                        Vars.set("volume", round(slider.getValue()));
                        break;
                    case "effects-volume":
                        Vars.set("cl_effects_volume", round(slider.getValue()));
                        break;
                    case "music-volume":
                        Vars.set("cl_music_volume", round(slider.getValue()));
                        break;
                    default:
                        break;
                }

            });
            for (Node node : buttons) {
                if (!(node instanceof Button)) {
                    Console.error("Invalid node.");
                    return;
                }
                Button b = (Button) node;
                b.setOnMouseReleased(e -> setVolume(e, b));
            }
        }
    }

    public void setVolume(MouseEvent me, Button button) {
        // ToDo
    }

    private double round(double val) {
        return Math.round(val * Vars.i("sv_precision")) / (double) Vars.i("sv_precision");
    }
}
