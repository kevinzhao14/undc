package undc.graphics.fxml.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import undc.command.Console;
import undc.command.Vars;
import undc.graphics.SettingsScreen;

import java.util.Set;

/**
 * Class that controls the Audio FXML file.
 */
public class AudioController extends SettingsPageController {
    @FXML
    private VBox master;
    @FXML
    private TextField masterField;
    @FXML
    private TextField effectsField;
    @FXML
    private TextField musicField;

    private String active;

    /**
     * Sets up a listener for the sliders to show the percentage.
     */
    public void initialize() {
        Set<Node> sliders = master.lookupAll(".controls-grid Slider");
        for (Node n : sliders) {
            if (!(n instanceof Slider)) {
                Console.error("Invalid node.");
                return;
            }
            Slider slider = (Slider) n;
            TextField textfield = (TextField) slider.getParent().getChildrenUnmodifiable().get(1);
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
                default:
                    Console.error("Invalid slider.");
                    return;
            }
            textfield.setText(Math.round(slider.getValue() * 100) + "%");

            // Add listener to apply slider value to textfield as percent
            slider.valueProperty().addListener((arg01, arg11, arg2) -> {
                textfield.setText(Math.round(slider.getValue() * 100) + "%");
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
        }
    }

    public void activeTextField(MouseEvent e) {
        active = ((Control) e.getSource()).getId();
    }

    /**
     * Returns the TextField that is currently clicked on.
     * @return currently active (clicked on) TextField
     */
    private TextField getActiveField() {
        switch (active) {
            case "masterField":
                return masterField;
            case "effectsField":
                return effectsField;
            case "musicField":
                return musicField;
            default:
                Console.error("No valid TextField is active.");
                return null;
        }
    }

    /**
     * Handles changing the volume via TextField inputs.
     * @param e KeyEvent to recognize when the ENTER key is pressed to indicate the user has finished inputting the
     *          desired volume.
     */
    public void setVolume(KeyEvent e) {
        double volume;
        if (e.getCode() == KeyCode.ENTER) {

            TextField textfield = getActiveField();
            if (textfield == null) {
                Console.error("Invalid TextField.");
                return;
            }
            volume = round(Double.parseDouble(textfield.getText()) / 100);

            Slider slider = (Slider) textfield.getParent().getChildrenUnmodifiable().get(0);
            slider.setValue(volume);
        }
    }

    private double round(double val) {
        return Math.round(val * Vars.i("sv_precision")) / (double) Vars.i("sv_precision");
    }

    public void reset() {
        SettingsScreen.getInstance().showPopup(this);
    }

    /**
     * Resets sliders, vars, and textfields to default values.
     */
    private void resetSliders() {
        Set<Node> sliders = master.lookupAll(".controls-grid Slider");
        for (Node n : sliders) {
            if (!(n instanceof Slider)) {
                Console.error("Invalid node.");
                return;
            }

            Vars.set("volume", 0.5);
            Vars.set("cl_effects_volume", 0.5);
            Vars.set("cl_music_volume", 0.5);

            ((Slider) n).setValue(Vars.d("volume"));
        }
    }

    public void resetSettings() {
        resetSliders();
        SettingsScreen.getInstance().showPopup(this);
    }
}
