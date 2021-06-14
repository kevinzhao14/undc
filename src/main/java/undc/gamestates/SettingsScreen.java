package undc.gamestates;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import undc.fxml.controllers.SettingsPageController;
import undc.handlers.Vars;
import undc.objects.Overlay;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Represents the setting screen. The settings screen is used by the player to modify in-game controls.
 */
public class SettingsScreen extends GameState {
    private static SettingsScreen instance;

    private ResetPopup popup;

    /**
     * Constructor for SettingsScreen.
     * @param width Width of the screen
     * @param height Height of the screen
     */
    private SettingsScreen(int width, int height) {
        super(width, height);
        try {
            URL url = new File("src/main/java/undc/fxml/SettingsScreen.fxml").toURI().toURL();
            Parent root = FXMLLoader.load(url);

            StackPane pane = new StackPane();
            popup = new ResetPopup();
            popup.toggle();
            pane.getChildren().addAll(root, popup.getRoot());

            scene = new Scene(pane, this.width, this.height);
            scene.getStylesheets().add("styles/global.css");
            scene.getStylesheets().add("styles/settings.css");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to retrieve the singleton instance of SettingsScreen.
     * @return the singleton instance of SettingsScreen
     */
    public static SettingsScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new SettingsScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }

    public void showPopup(SettingsPageController page) {
        popup.setPage(page);
        popup.toggle();
    }

    private class ResetPopup extends Overlay {

        private SettingsPageController page;

        /**
         * Constructor that designs the graphics for the popup displayed when reset is clicked.
         */
        private ResetPopup() {
            HBox popupContainer = new HBox();
            popupContainer.setFillHeight(false);
            popupContainer.setAlignment(Pos.CENTER);

            VBox popup = new VBox();
            popup.setId("popup");

            HBox titleBox = new HBox();
            titleBox.setId("popup-title");
            Label title = new Label("Reset Settings");
            titleBox.getChildren().add(title);

            Label warning = new Label("Are you sure you want to reset your game settings?");
            warning.setId("popup-warning");

            Button reset = new Button("Reset");
            reset.setOnAction(e -> page.resetSettings());

            Button cancel = new Button("Cancel");
            cancel.setOnAction(e -> toggle());

            HBox buttonsContainer = new HBox();
            buttonsContainer.setId("popup-buttons-container");
            buttonsContainer.getChildren().addAll(reset, cancel);

            popup.getChildren().addAll(titleBox, warning, buttonsContainer);

            popupContainer.getChildren().add(popup);
            root.getChildren().add(popupContainer);
        }

        public void setPage(SettingsPageController page) {
            this.page = page;
        }
    }
}
