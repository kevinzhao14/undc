package undc.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import undc.gamestates.*;
import undc.handlers.*;
import undc.objects.*;

import java.util.*;

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
        if (echo) print(PREFIX + command);
        String[] cmd = command.split(" ");
        switch (cmd[0].toLowerCase()) {
            case "echo":
                print(cmd[1]);
                break;
            case "bind":
                if (cmd.length < 2) {
                    error("Invalid arguments for bind.");
                    return;
                }
                String key = clean(cmd[1]);
                if (cmd.length == 3) {
                    String control = clean(cmd[2]);
                    Controls.getInstance().setKey(key, control);
                    if (!silent) print("Key bound.");
                } else if (cmd.length == 2) {
                    String control = Controls.getInstance().getControl(key);
                    if (control.equals("")) error("Key is not bound.");
                    else print(control);
                }
                break;
            case "unbind":
                if (cmd.length < 2) {
                    error("Invalid arguments for unbind.");
                    return;
                }
                key = clean(cmd[1]);
                Controls.getInstance().removeKey(key);
                break;
            case "set":
                if (cmd.length < 3) {
                    error("Invalid arguments for set.");
                    return;
                }
                String var = clean(cmd[1]);
                String val = clean(cmd[2]);
                Vars.set(var, val);
                break;
            case "cvar":
                if (cmd.length < 2) {
                   error("Invalid arguments for cvar.");
                   return;
                }
                var = clean(cmd[1]);
                CVar cvar = Vars.find(var);
                if (cvar instanceof BooleanCVar) {
                    BooleanCVar v = (BooleanCVar) cvar;
                    print("" + v.getVal() + " (default: " + v.getDef() + ")");
                } else if (cvar instanceof StringCVar) {
                    StringCVar v = (StringCVar) cvar;
                    print(v.getVal() + " (default: " + v.getDef() + ")");
                } else if (cvar instanceof IntCVar) {
                    IntCVar v = (IntCVar) cvar;
                    print("" + v.getVal() + " (default: " + v.getDef() + ", min: " + v.getMin() + ", max: " + v.getMax() + ")");
                } else if (cvar instanceof  DoubleCVar) {
                    DoubleCVar v = (DoubleCVar) cvar;
                    print("" + v.getVal() + " (default: " + v.getDef() + ", min: " + v.getMin() + ", max: " + v.getMax() + ")");
                } else {
                    error("Could not find cvar.");
                }
                break;
            default:
                error("Unrecognized command.");
                break;
        }
    }

    private static String clean(String s) {
        return s.toLowerCase().replaceAll("[\"']", "");
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

        //applyCss & layout need to be called for the ScrollPane to recalculate its size so that setting the vvalue works properly
        historyScroll.applyCss();
        historyScroll.layout();
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
        historyScroll.setId("history-scroll");

        historyBox = new VBox();
        historyBox.setPrefWidth(WIDTH);
        historyBox.setPrefHeight(HEIGHT - 30);
        historyBox.setId("history-box");
        refresh();

        input = new TextField();
        input.setPrefHeight(30);
        input.setMaxWidth(WIDTH);
        input.setId("input");

        input.setOnAction((e) -> {
            run(input.getText());
            input.clear();
        });

        input.setOnKeyPressed(e -> handleKey(e.getCode().toString()));

        historyScroll.setContent(historyBox);
        box.getChildren().addAll(historyScroll, input);
        scene.getChildren().add(box);
        scene.getStylesheets().add("styles/console.css");
//        scene.getStylesheets().add("styles/caspian.css");
    }

    private static void handleKey(String key) {
        String control = Controls.getInstance().getControl(key);

        if (control.equals("console")) {
            GameScreen.getInstance().toggleConsole();
            GameController.getInstance().pause();
        }
    }
}
