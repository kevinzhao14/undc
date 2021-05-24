package undc.gamestates;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import undc.controllers.Controller;

public class SettingsScreen extends GameState{

    SettingsScreen(int width, int height) {
        super(width, height);

        // Creating the nodes for the Settings screen
        Label pageHeader = new Label("Options");
        pageHeader.setId("pageHeader");

        // Vbox will had the buttons for the different settings tabs
        VBox vBox = new VBox(7);
        Button generalBtn = new Button("General");
        Button videoBtn = new Button("Video");
        Button audioBtn = new Button("Audio");
        Button keyMouseBtn = new Button("Keyboard/Mouse");
        vBox.getChildren().addAll(generalBtn, videoBtn, audioBtn, keyMouseBtn);

        // menu will hold the panes that will display the actual settings the user changes
        StackPane menu = new StackPane();
        menu.setId("menu");

        Button closeBtn = new Button("Close Settings");
        closeBtn.setId("close-button");

        Button saveBtn = new Button("Save");
        saveBtn.setId("save-button");
        Button resetBtn = new Button("Reset");
        resetBtn.setId("reset-button");
        HBox hBox = new HBox(7);
        hBox.getChildren().addAll(saveBtn, resetBtn);

        // Panes for the menu and each tab button
        Pane generalPane = new Pane();
        generalPane.setId("general-pane");

        Pane videoPane = new Pane();
        videoPane.setId("video-pane");
        videoPane.setVisible(false);

        Pane audioPane = new Pane();
        audioPane.setId("audio-pane");
        audioPane.setVisible(false);

        Pane keyMousePane = new Pane();
        keyMousePane.setId("keyMouse-pane");
        keyMousePane.setVisible(false);

        menu.getChildren().addAll(generalPane, videoPane, audioPane, keyMousePane);

        Pane root = new Pane();
        root.getChildren().addAll(pageHeader, vBox, closeBtn, hBox, menu);


        // Setting the positions of nodes on the pane
        pageHeader.setLayoutX(14);
        pageHeader.setLayoutY(14);

        vBox.setLayoutX(14);
        vBox.setLayoutY(80);

        closeBtn.setLayoutX(14);
        closeBtn.setLayoutY(477);

        hBox.setLayoutX(400);
        hBox.setLayoutY(477);

        menu.setLayoutX(218);
        menu.setLayoutY(80);

        scene = new Scene(root, this.width, this.height);
        scene.getStylesheets().addAll("styles/settings.css");

        // Event handling for buttons
        closeBtn.setOnAction(event -> {
            //System.out.println("close button pressed");
            Controller.setState(new HomeScreen(1920, 1080));
        });

        generalBtn.setOnAction(event -> {
            for (Node pane : menu.getChildren()) {
                if (pane.isVisible()) {
                    pane.setVisible(false);
                }
            }
            generalPane.setVisible(true);
            generalBtn.setStyle("-fx-background-color: #5E6583");
            /* Unfinished code for changing style back to normal when another button is pressed
            for (Node btn : vBox.getChildren()) {
                if (btn != generalBtn) {
                    if (!(btn.getStyle().equals("-fx-background-color: #090a0c"))) {
                        btn.setStyle("-fx-background-color: #090a0c");
                    }
                }
            }
             */
        });

        videoBtn.setOnAction(event -> {
            for (Node pane : menu.getChildren()) {
                if (pane.isVisible()) {
                    pane.setVisible(false);
                }
            }
            videoBtn.setStyle("-fx-background-color: #5E6583");
            videoPane.setVisible(true);
        });

        audioBtn.setOnAction(event -> {
            for (Node pane : menu.getChildren()) {
                if (pane.isVisible()) {
                    pane.setVisible(false);
                }
            }
            audioBtn.setStyle("-fx-background-color: #5E6583");
            audioPane.setVisible(true);
        });

        keyMouseBtn.setOnAction(event -> {
            for (Node pane : menu.getChildren()) {
                if (pane.isVisible()) {
                    pane.setVisible(false);
                }
            }
            keyMouseBtn.setStyle("-fx-background-color: #5E6583");
            keyMousePane.setVisible(true);
        });

        saveBtn.setOnAction(event -> {
            System.out.println("Save button pressed.");
        });

        resetBtn.setOnAction(event -> {
            System.out.println("Reset button pressed.");
        });
    }
}
