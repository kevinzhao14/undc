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

import java.util.ArrayList;
import java.util.Arrays;
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
    private static final LinkedList<Label> history = new LinkedList<>(); // List of all messages, max size of MAX_SIZE
    static final ArrayList<Command> commands = Command.load(); // List of all available commands
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
    private static boolean silent;

    /**
     * Prints a message to the console in a specified color.
     * @param str Message to print to the console
     * @param color Color of the message
     */
    private static void print(String str, String color) {
        if (silent) {
            return;
        }
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

        Console.silent = silent;
        String[] cmd = command.split(" ");
        String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);

        for (Command c : commands) {
            if (c.getName().equalsIgnoreCase(cmd[0])) {
                c.run(args);
                Console.silent = false;
                return;
            }
        }

        error("Invalid command.");
        Console.silent = false;
    }

    public static void run(String command, boolean echo) {
        run(command, echo, false);
    }

    public static void run(String command) {
        run(command, true, false);
    }

    /**
     * Refreshes the JavaFX elements to display any new messages.
     */
    private static void refresh() {
        if (historyBox == null) {
            create();
        }
        historyBox.getChildren().clear();
        historyBox.getChildren().addAll(history);

        //applyCss & layout need to be called for the ScrollPane to recalculate its size so that
        //setting the vvalue works properly
        historyScroll.applyCss();
        historyScroll.layout();
        historyScroll.setVvalue(1);
    }

    public static void clear() {
        history.clear();
        refresh();
        historyScroll.setVvalue(0);
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
        // don't recreate the console if it already exists. If necessary, create an override method that sets scene to
        // null before calling this method.
        if (scene != null) {
            return;
        }
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
        historyScroll.setVvalue(0);
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

        if (control.equals("console") || control.equals("pause")) { // toggle console
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
