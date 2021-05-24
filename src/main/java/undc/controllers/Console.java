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
import undc.handlers.*;

import java.util.LinkedList;

public class Console {
    private static final int MAX_SIZE = 100;
    private static final String PREFIX = "> ";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
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

    public static void run(String command, boolean echo, boolean silent) {
        if (echo) add(PREFIX + command);
        String[] cmd = command.split(" ");
        switch (cmd[0].toLowerCase()) {
            case "echo":
                add(cmd[1]);
                break;
            case "bind":
                if (cmd.length < 2) {
                    error("Invalid arguments for bind.");
                    return;
                }
                cmd[1] = cmd[1].toLowerCase().replaceAll("[\"']", "");
                if (cmd.length == 3) {
                    cmd[2] = cmd[2].toLowerCase().replaceAll("[\"']", "");
                    Controls.getInstance().setKey(cmd[1], cmd[2]);
                    if (!silent) print("Key bound.");
                } else if (cmd.length == 2) {
                    String control = Controls.getInstance().getControl(cmd[1]);
                    if (control.equals("")) error("Key is not bound.");
                    else print(control);
                }
                break;
            case "unbind" :
                if (cmd.length < 2) {
                    error("Invalid arguments for unbind.");
                    return;
                }
                String key = cmd[1].toLowerCase().replaceAll("[\"']", "");
                Controls.getInstance().removeKey(key);
                break;
            default:
                add("Unrecognized command.");
                break;
        }
    }

    public static void run(String command, boolean echo) {
        run(command, echo, false);
    }

    public static void run(String command) {
        run(command, true, false);
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
        refresh();
    }

    private static void add(String message) {
        add(message, "black");
    }

    private static void refresh() {
        historyBox.getChildren().clear();
        historyBox.getChildren().addAll(history);
        historyScroll.setVvalue(2);
    }

    public static Pane getScene() {
        if (scene == null) create();
        return scene;
    }

    public static void create() {
        scene = new Pane();
        VBox box = new VBox();
        box.setId("box");
        box.setTranslateY(Vars.i("gc_screen_height") - HEIGHT);

        historyScroll = new ScrollPane();
        historyScroll.setFitToWidth(true);

        historyBox = new VBox();
        historyBox.setPrefWidth(WIDTH);
        historyBox.setPrefHeight(HEIGHT - 30);
        refresh();

        input = new TextField();
        input.setPrefHeight(30);
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
