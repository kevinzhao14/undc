package undc.controllers;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.LinkedList;

public class Console {
    private static final int MAX_SIZE = 100;
    private static final String PREFIX = "> ";
    private static final int WIDTH = 400;
    private static final int HEIGHT = 400;
    private static final Font FONT = new Font("Monospaced", 12);


    private static Pane scene;
    private static LinkedList<Label> history = new LinkedList<>();
    private static VBox historyBox;
    private static ScrollPane historyScroll;
    private static TextField input;

    private static void print(String str, String color) {
        add(str, color);
    }

    public static void print(String str) {
        print(str, "black");
    }

    public static void warn(String str) {
        print(str, "#ffc107");
    }

    public static void error(String str) {
        print(str, "red");
    }

    public static void run(String command, boolean echo) {
        if (echo) add(PREFIX + command);
        String[] cmd = command.split(" ");
        switch (cmd[0].toLowerCase()) {
            case "echo":
                add(cmd[1]);
                break;
            default:
                add("Unrecognized command.");
                break;
        }
    }

    private static void add(String message, String color) {
        if (history.size() >= MAX_SIZE) history.remove();
        Label temp = new Label(message);
        temp.setMaxWidth(WIDTH);
        temp.setWrapText(true);
        temp.setTextFill(Color.web(color));
        temp.setStyle("-fx-padding: 2px");
        temp.setFont(FONT);
        history.add(temp);
        Platform.runLater(() -> refresh());
    }

    private static void add(String message) {
        add(message, "black");
    }

    public static void run(String command) {
        run(command, true);
    }

    private static void refresh() {
        historyBox.getChildren().clear();
        historyBox.getChildren().addAll(history);
        historyScroll.setVvalue(1);
    }

    public static Pane getScene() {
        if (scene == null) create();
        return scene;
    }

    public static void create() {
        scene = new Pane();
        VBox box = new VBox();
        box.setId("box");

        historyScroll = new ScrollPane();
        historyScroll.setFitToWidth(true);

        historyBox = new VBox();
        historyBox.setPrefWidth(WIDTH);
        historyBox.setPrefHeight(HEIGHT - 50);
        refresh();

        input = new TextField();
        input.setPrefHeight(50);
        input.setMaxWidth(WIDTH);
        input.setId("input");

        input.setOnAction((e) -> {
            run(input.getText());
            input.clear();
        });

        historyScroll.setContent(historyBox);
        box.getChildren().addAll(historyScroll, input);
        scene.getChildren().add(box);
        scene.getStylesheets().add("styles/console.css");
    }
}
