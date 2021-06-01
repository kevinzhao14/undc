package undc.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import undc.gamestates.GameScreen;
import undc.handlers.Controls;
import undc.handlers.Vars;
import undc.objects.CVar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Class that handles the Console in game. Is used by other classes to print information and by the player to execute
 * commands.
 */
public class Console {
    /*
     * Static variables representing configurable values.
     */
    private static final int MAX_SIZE = 200; // Maximum number of messages the console stores
    private static final String PREFIX = "> "; // Prefix for printing the user's command
    private static final int WIDTH = 600; // Default width of the console
    private static final int HEIGHT = 600; // Default height of the console
    private static final HashMap<String, String> commands = new HashMap<>(); // List of all available commands
    private static final LinkedList<Label> history = new LinkedList<>(); // List of all messages, max size of MAX_SIZE
    // List of all issued commands by the user
    private static final ArrayList<String> commandHistory = new ArrayList<>();

    /*
     * Modified Variables
     */
    private static Pane scene; // Main Parent of the console
    private static int commandPos = -1; // Position of the command retrieved when using up/down arrows
    private static VBox historyBox; // Box that contains all the messages
    private static ScrollPane historyScroll; // Pane that does the scrolling
    private static TextField input; // Input field

    /**
     * Loads the commands list, the list used with the "find" command.
     */
    private static void loadCommandsList() {
        commands.put("bind key", "bind <key> <command> - Binds a key to a command.");
        commands.put("unbind key", "unbind <key> - Unbinds a key.");
        commands.put("set cvar", "<cvar> <value> - Sets the value of a cvar.");
        commands.put("get cvar", "<cvar> - Gets the value of a cvar.");
        commands.put("find command", "find <search> - Finds commands with a search value.");
        commands.put("reset", "reset <cvar> - Resets a cvar.");
        commands.put("clear console", "clear - Clears the console.");
    }

    /**
     * Prints a message to the console in a specified color.
     * @param str Message to print to the console
     * @param color Color of the message
     */
    private static void print(String str, String color) {
        System.out.println(str);
        if (history.size() >= MAX_SIZE) {
            history.remove();
        }
        // create temporary Label and add it to the history
        Label temp = new Label(str);
        temp.setMaxWidth(WIDTH);
        temp.setWrapText(true);
        temp.setTextFill(Color.web(color));
        temp.setStyle("-fx-padding: 2px");
        history.add(temp);
        refresh();
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

    /**
     * Runs a command.
     * @param command Command to run
     * @param echo Whether to print the command to the console
     * @param silent Whether to suppress all console outputs
     */
    public static void run(String command, boolean echo, boolean silent) {
        if (echo) {
            print(PREFIX + command);
        }
        String[] cmd = command.split(" ");
        switch (cmd[0].toLowerCase()) {
            case "echo": // Echos the message back to the user
                print(cmd[1]);
                break;
            case "bind": // Binds a command to a key or retrieves the command bound to a key.
                if (cmd.length < 2) {
                    error("Invalid arguments for bind.");
                    return;
                }
                String key = clean(cmd[1]);
                if (cmd.length == 3) { // New bind. Format: bind <key> <command>
                    String control = clean(cmd[2]);
                    Controls.getInstance().setKey(key, control);
                    if (!silent) {
                        print("Key bound.");
                    }
                } else if (cmd.length == 2) { // Retrieval. Format: bind <key>
                    String control = Controls.getInstance().getControl(key);
                    if (control.equals("")) {
                        error("Key is not bound.");
                    } else {
                        print(control);
                    }
                }
                break;
            case "unbind": // Unbinds a key. Format: unbind <key>
                if (cmd.length < 2) {
                    error("Invalid arguments for unbind.");
                    return;
                }
                key = clean(cmd[1]);
                Controls.getInstance().removeKey(key);
                break;
            case "set": // Sets a CVar. Format: set <cvar> <value>
                if (cmd.length < 3) {
                    error("Invalid arguments for set.");
                    return;
                }
                String var = clean(cmd[1]);
                String val = clean(cmd[2]);
                if (Vars.set(var, val)) {
                    print(var + " has been set to " + val);
                } else if (!silent) {
                    error("Failed to set " + var + ".");
                }
                break;
            case "get": // Retrieves the value of a CVar. Format: get <cvar>
                if (cmd.length < 2) {
                    error("Invalid arguments for get.");
                    return;
                }
                var = clean(cmd[1]);
                CVar cvar = Vars.find(var);
                if (cvar == null) {
                    if (!silent) {
                        error("CVar could not be found.");
                        return;
                    }
                } else {
                    print(cvar.toString());
                }
                break;
            case "reset": // Resets the value of a CVar. Format: reset <cvar>
                if (cmd.length < 2) {
                    error("Invalid arguments for reset.");
                    return;
                }
                var = clean(cmd[1]);
                cvar = Vars.find(var);
                if (cvar == null) {
                    error("Could not find " + var);
                    return;
                }
                cvar.reset();
                print(var + " was reset.");
                break;
            case "find": // Searches for commands. Format: find <search>
                if (cmd.length < 2) {
                    run("find \"\"", false);
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
            case "clear": // Clears the console's history. Format: clear
                history.clear();
                refresh();
                historyScroll.setVvalue(0);
                break;
            default: // Defaults to modifying/retrieving a CVar.
                if (Vars.find(clean(cmd[0])) != null) {
                    if (cmd.length == 1) {
                        run("get " + command, false);
                    } else if (cmd.length == 2) {
                        run("set " + command, false);
                    }
                } else {
                    error("Unrecognized command.");
                }
                break;
        }
    }

    public static void run(String command, boolean echo) {
        run(command, echo, false);
    }

    public static void run(String command) {
        run(command, true, false);
    }

    private static String clean(String s) {
        return s.toLowerCase().replaceAll("[\"']", "");
    }

    /**
     * Refreshes the JavaFX elements to display any new messages.
     */
    private static void refresh() {
        historyBox.getChildren().clear();
        historyBox.getChildren().addAll(history);

        //applyCss & layout need to be called for the ScrollPane to recalculate its size so that
        //setting the vvalue works properly
        historyScroll.applyCss();
        historyScroll.layout();
        historyScroll.setVvalue(2);
    }

    /**
     * Returns the JavaFX root for the Console.
     * @return Returns the Pane that holds the Console
     */
    public static Pane getScene() {
        if (scene == null) {
            create();
        }
        return scene;
    }

    /**
     * Creates the JavaFX for the Console.
     */
    public static void create() {
        loadCommandsList();

        // overall containers
        scene = new Pane();
        VBox box = new VBox();
        box.setId("box");

        // set size & positioning for the console box
        box.setPrefHeight(HEIGHT);
        box.setPrefWidth(WIDTH);
        box.setLayoutX(50);
        box.setLayoutY(50);
        // makes the console draggable, via Draggable library
        new Draggable.Nature(box);

        // stuff for the close button that is actually a label
        HBox closeBtnBox = new HBox();
        Label closeBtn = new Label("×");
        closeBtnBox.getChildren().add(closeBtn);
        closeBtnBox.setPrefWidth(WIDTH);
        closeBtnBox.setId("close-button-box");
        closeBtn.setId("close-button");

        closeBtn.setOnMouseClicked(e -> {
            GameScreen.getInstance().toggleConsole();
            GameController.getInstance().pause();
        });

        // stuff for the scrollpane & messages box
        historyScroll = new ScrollPane();
        historyScroll.setFitToWidth(true);
        historyScroll.setId("history-scroll");

        historyBox = new VBox();
        historyBox.setPrefWidth(WIDTH);
        historyBox.setPrefHeight(HEIGHT - 30);
        historyBox.setId("history-box");
        refresh();
        // controls the scroll speed of the scrollpane
        historyBox.setOnScroll(e -> historyScroll.setVvalue(historyScroll.getVvalue() - e.getDeltaY() * 0.0025));

        VBox spacer = new VBox();
        spacer.getStyleClass().add("spacer");

        // stuff for the input field
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
        box.getChildren().addAll(closeBtnBox, historyScroll, spacer, input);
        scene.getChildren().add(box);
        scene.getStylesheets().add("styles/console.css");
    }

    /**
     * Loads a previous command into the input box, based on commandPos and commandHistory.
     */
    private static void loadCommand() {
        if (commandPos < -1 || commandPos >= commandHistory.size()) {
            error("Could not load command. Position out of bounds.");
            return;
        }
        String command = commandPos == -1 ? "" : commandHistory.get(commandPos);
        input.setText(command);
        input.positionCaret(command.length());
    }

    /**
     * Handles key presses.
     * @param key Key that was pressed
     */
    private static void handleKey(String key) {
        String control = Controls.getInstance().getControl(key);

        if (control.equals("console")) { // toggle console
            GameScreen.getInstance().toggleConsole();
            GameController.getInstance().pause();
        } else if (key.equals("UP")) { // go to previous command
            commandPos++;
            if (commandPos >= commandHistory.size()) {
                commandPos = commandHistory.size() - 1;
            }
            loadCommand();
        } else if (key.equals("DOWN")) { // go to next command
            commandPos--;
            if (commandPos < -1) {
                commandPos = -1;
            }
            loadCommand();
        }
    }
}
