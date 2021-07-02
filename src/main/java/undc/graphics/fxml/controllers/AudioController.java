package undc.graphics.fxml.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import undc.command.Console;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * Class that controls the Audio FXML file.
 */
public class AudioController implements Initializable {

    @FXML
    VBox master;

    int percent;
    Set<Node> sliders;

    /**
     * Sets up a listener for the sliders to show the percentage.
     * @param arg0 URL
     * @param arg1 ResourceBundle
     */
    public void initialize(URL arg0, ResourceBundle arg1) {
        sliders = master.lookupAll(".controls-grid Slider");
        for (Node n : sliders) {
            if (!(n instanceof Slider)) {
                Console.error("invalid node");
                return;
            }
            ((Slider) n).valueProperty().addListener(new ChangeListener<Number>() {

                @Override
                public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                    percent = (int) ((Slider) n).getValue();
                    ((Button) ((Slider) n).getParent().getChildrenUnmodifiable().get(1)).setText(percent + "%");

                }
            });
        }
    }
}
