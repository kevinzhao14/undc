package undc.controllers;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import undc.gamestates.GameScreen;
import undc.handlers.Controls;
import undc.handlers.DraggableNode;
import undc.handlers.ResizableNode;

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
    private static final LinkedList<Label> HISTORY = new LinkedList<>(); // List of all messages, max size of MAX_SIZE
    static final ArrayList<Command> COMMANDS = Command.load(); // List of all available commands
    // List of all issued commands by the user
    private static final ArrayList<String> COMMAND_HISTORY = new ArrayList<>();

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
        if (HISTORY.size() >= MAX_SIZE) {
            HISTORY.remove();
        }
        // create temporary Label and add it to the history
        Label temp = new Label(str);
        temp.setWrapText(true);
        temp.setTextFill(Color.web(color));
        temp.setStyle("-fx-padding: 2px");
        HISTORY.add(temp);
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

        for (Command c : COMMANDS) {
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
        historyBox.getChildren().addAll(HISTORY);

        //applyCss & layout need to be called for the ScrollPane to recalculate its size so that
        //setting the vvalue works properly
        historyScroll.applyCss();
        historyScroll.layout();
        historyScroll.setVvalue(1);
    }

    /**
     * Clears the console of all messages.
     */
    public static void clear() {
        HISTORY.clear();
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
        // overall container
        scene = new Pane();

        // Box of the console - ie the actual console itself
        BorderPane box = new BorderPane();
        box.setId("box");
        box.setPrefSize(WIDTH, HEIGHT);
        box.setMinSize(WIDTH / 2.0, HEIGHT / 2.0);
        box.setMaxSize(WIDTH * 3, HEIGHT * 3);
        box.setLayoutX(50);
        box.setLayoutY(50);
        // makes the console draggable, via Draggable library
        DraggableNode.add(box);

        // stuff for the close button that is actually a label
        Label closeBtn = new Label("×");
        closeBtn.setId("close-button");
        closeBtn.setOnMouseClicked(e -> {
            GameScreen.getInstance().toggleConsole();
            GameController.getInstance().pause();
        });

        // container for the close button
        HBox closeBtnBox = new HBox();
        closeBtnBox.getChildren().add(closeBtn);
        closeBtnBox.setId("close-button-box");

        // stuff for the scrollpane & messages box
        historyScroll = new ScrollPane();
        historyScroll.setId("history-scroll");

        // box that holds the messages
        historyBox = new VBox();
        historyBox.setId("history-box");
        // controls the scroll speed of the scrollpane
        historyBox.setOnScroll(e -> historyScroll.setVvalue(historyScroll.getVvalue() - e.getDeltaY() * 0.0025));

        // stuff for the input field
        input = new TextField();
        input.setId("input");

        input.setOnAction((e) -> {
            commandPos = -1;
            COMMAND_HISTORY.add(0, input.getText());
            run(input.getText());
            input.clear();
        });

        input.setOnKeyPressed(e -> handleKey(e.getCode().toString()));

        // image/area used to resize the console
        ImageView resizeArea = new ImageView("icons/resizable.png");
        resizeArea.setId("resize");
        resizeArea.setPickOnBounds(true); // required for transparent pixels to fire mouse events
        // size needs to be set here because there's no option in css :(
        resizeArea.setFitWidth(8);
        resizeArea.setFitHeight(8);

        // container for the resize area
        HBox resizeContainer = new HBox();
        resizeContainer.getChildren().add(resizeArea);
        resizeContainer.setId("resize-container");
        ResizableNode.add(resizeArea, ResizableNode.ResizeDirection.ALL, box);

        // box that holds the stuff on the bottom (input & resize area)
        VBox bottomBox = new VBox();
        bottomBox.setId("bottombox");
        bottomBox.getChildren().addAll(input, resizeContainer);

        // add everything
        historyScroll.setContent(historyBox);
        box.setTop(closeBtnBox);
        box.setCenter(historyScroll);
        box.setBottom(bottomBox);
        scene.getChildren().add(box);
        scene.getStylesheets().add("styles/console.css");
    }

    /**
     * Loads a previous command into the input box, based on commandPos and commandHistory.
     */
    private static void loadCommand() {
        if (commandPos < -1 || commandPos >= COMMAND_HISTORY.size()) {
            error("Could not load command. Position out of bounds.");
            return;
        }
        String command = commandPos == -1 ? "" : COMMAND_HISTORY.get(commandPos);
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
            if (commandPos >= COMMAND_HISTORY.size()) {
                commandPos = COMMAND_HISTORY.size() - 1;
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
