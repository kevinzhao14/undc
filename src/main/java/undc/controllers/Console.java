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
    private static final int MAX_SIZE = 200;
    private static final String PREFIX = "> ";
    private static final int WIDTH = 600;
    private static final int HEIGHT = 600;
    private static final Font FONT = new Font("Monospaced", 12);
    private static final HashMap<String, String> commands = new HashMap<>();


    private static Pane scene;
    private static LinkedList<Label> history = new LinkedList<>();
    private static ArrayList<String> commandHistory = new ArrayList<>();
    private static int commandPos = -1;
    private static VBox historyBox;
    private static ScrollPane historyScroll;
    private static TextField input;

    private static void loadCommandsList() {
        commands.put("bind key", "bind <key> <command> - Binds a key to a command.");
        commands.put("unbind key", "unbind <key> - Unbinds a key.");
        commands.put("set cvar", "<cvar> <value> - Sets the value of a cvar.");
        commands.put("get cvar", "<cvar> - Gets the value of a cvar.");
        commands.put("find command", "find <search> - Finds commands with a search value.");
    }

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
                if (Vars.set(var, val)) {
                    print(var + " has been set to " + val);
                } else if (!silent) {
                    error("Failed to set " + var);
                }
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
                } else if (!silent) {
                    error(var + " could not be found.");
                }
                break;
            case "find":
                if (cmd.length < 2) {
                    error("Invalid arguments for find.");
                    return;
                }
                String search = clean(cmd[1]);
                StringBuilder res = new StringBuilder();
                for (HashMap.Entry<String, String> e : commands.entrySet()) {
                    if (e.getKey().contains(search)) {
                        res.append(e.getValue());
                        res.append("\n");
                    }
                }
                if (res.toString().equals("")) {
                    res.append("No results found.");
                }
                print(res.toString());
                break;
            case "clear":
                history.clear();
                refresh();
                break;
            default:
                if (Vars.find(clean(cmd[0])) != null) {
                    if (cmd.length == 1) {
                        run("cvar " + command, false);
                    } else if (cmd.length == 2) {
                        run("set " + command, false);
                    }
                } else {
                    error("Unrecognized command.");
                }
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
        loadCommandsList();

        scene = new Pane();
        VBox box = new VBox();
        box.setId("box");

        box.setPrefHeight(HEIGHT);
        box.setPrefWidth(WIDTH);
        Draggable.Nature nature = new Draggable.Nature(box);

        historyScroll = new ScrollPane();
        historyScroll.setFitToWidth(true);
        historyScroll.setId("history-scroll");

        historyBox = new VBox();
        historyBox.setPrefWidth(WIDTH);
        historyBox.setPrefHeight(HEIGHT - 30);
        historyBox.setId("history-box");
        refresh();

        VBox spacer = new VBox();
        spacer.getStyleClass().add("spacer");

        input = new TextField();
        input.setPrefHeight(30);
        input.setMaxWidth(WIDTH);
        input.setId("input");

        input.setOnAction((e) -> {
            commandPos = -1;
            commandHistory.add(0, input.getText());
            run(input.getText());
            input.clear();
        });

        input.setOnKeyPressed(e -> handleKey(e.getCode().toString()));

        historyScroll.setContent(historyBox);
        box.getChildren().addAll(historyScroll, spacer, input);
        scene.getChildren().add(box);
        scene.getStylesheets().add("styles/console.css");
    }

    private static void loadCommand() {
        if (commandPos < -1 || commandPos >= commandHistory.size()) {
            error("Could not load command. Position out of bounds.");
            return;
        }
        String command = commandPos == -1 ? "" : commandHistory.get(commandPos);
        input.setText(command);
        input.positionCaret(command.length());
    }

    private static void handleKey(String key) {
        String control = Controls.getInstance().getControl(key);

        if (control.equals("console")) {
            GameScreen.getInstance().toggleConsole();
            GameController.getInstance().pause();
        } else if (key.equals("UP")) {
            commandPos++;
            if (commandPos >= commandHistory.size()) {
                commandPos = commandHistory.size() - 1;
            }
            loadCommand();
        } else if (key.equals("DOWN")) {
            commandPos--;
            if (commandPos < -1) {
                commandPos = -1;
            }
            loadCommand();
        }
    }
}
