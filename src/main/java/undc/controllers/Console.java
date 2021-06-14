package undc.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
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
    private static final int WIDTH = 600; // Default width of the console
    private static final int HEIGHT = 600; // Default height of the console
    private static final int OFF_X = 50; // Horizontal offset of the console
    private static final int OFF_Y = 50; // Vertical offset of the console

    private static final int MAX_SIZE = 200; // Maximum number of messages the console stores
    private static final int SUGG_MAX_SIZE = 10;
    private static final String PREFIX = "> "; // Prefix for printing the user's command
    private static final LinkedList<String> HISTORY = new LinkedList<>(); // List of all messages, max size of MAX_SIZE
    // List of all issued commands by the user
    private static final ArrayList<String> COMMAND_HISTORY = new ArrayList<>();
    private static final ArrayList<Label> SUGGESTIONS = new ArrayList<>(); // List of suggested/autocomplete commands
    static final ArrayList<Command> COMMANDS = Command.load(); // List of all available commands

    /*
     * Modified Variables
     */
    private static Pane scene; // Main Parent of the console
    private static WebView historyBox; // Box that contains all the messages
    private static ScrollPane historyScroll; // Pane that does the scrolling
    private static TextField input; // Input field
    private static VBox suggestBox; // VBox that holds the suggestions
    private static int commandPos = -1; // Position of the command retrieved when using up/down arrows
    private static boolean muted; // Whether or not messages should be printed
    private static String tempCommand = ""; // storage for the new command

    /**
     * Prints a message to the console in a specified color.
     * @param str Message to print to the console
     * @param color Color of the message
     */
    private static void print(String str, String color) {
        if (!muted) {
            System.out.println(str);
            if (HISTORY.size() >= MAX_SIZE) {
                HISTORY.remove();
            }
            // create temporary Label and add it to the history; add newline if it's not the first message
            String temp = "<p style='color: " + color + ";'>" + str + "</p>";
            HISTORY.add(temp);
            refresh();
        }
    }

    public static void print(String str) {
        print(str, "black");
    }

    public static void warn(String str) {
        print(str, "#dd9900");
    }

    public static void error(String str) {
        print(str, "red");
    }

    /**
     * Runs a command.
     * @param command Command to run
     * @param echo Whether to print the command to the console
     * @param muted Whether to suppress all console outputs
     */
    public static void run(String command, boolean echo, boolean muted) {
        if (echo) {
            print(PREFIX + command);
        }
        Console.muted = muted;

        // get the arguments of the command
        String[] cmd = command.split(" ");
        String[] args = Arrays.copyOfRange(cmd, 1, cmd.length);

        // search for the command
        for (Command c : COMMANDS) {
            if (c.getName().equalsIgnoreCase(cmd[0])) {
                c.run(args);
                Console.muted = false;
                return;
            }
        }
        // no command found
        error("Invalid command.");
        Console.muted = false;
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
        StringBuilder str = new StringBuilder();
        for (String s : HISTORY) {
            str.append(s);
        }
        Platform.runLater(() -> {
            historyBox.getEngine().loadContent(str.toString(), "text/html");
            // set the css file to style the text and stuff
            historyBox.getEngine().setUserStyleSheetLocation("file:src/main/resources/styles/console.css");
            // scroll to bottom after the page has loaded/succeeded
            historyBox.getEngine().getLoadWorker().stateProperty().addListener((o, old, n) -> {
                if (n == Worker.State.SUCCEEDED) {
                    historyBox.getEngine().executeScript("window.scrollTo(0, document.body.scrollHeight);");
                }
            });
        });
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
        box.setLayoutX(OFF_X);
        box.setLayoutY(OFF_Y);
        // makes the console draggable, via Draggable library

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
        historyBox = new WebView();
        historyBox.setId("history-box");
        // disable the right-click menu
        historyBox.setContextMenuEnabled(false);
        // controls the scroll speed of the scrollpane
        historyBox.setOnScroll(e -> historyScroll.setVvalue(historyScroll.getVvalue() - e.getDeltaY() * 0.0025));
        refresh();

        // stuff for the input field
        input = new TextField();
        input.setId("input");

        input.setOnAction((e) -> {
            commandPos = 0;
            // add the command to the history if it's not the same as the previous command, to prevent clogging
            if (COMMAND_HISTORY.size() == 0 || !input.getText().equalsIgnoreCase(COMMAND_HISTORY.get(0))) {
                COMMAND_HISTORY.add(0, input.getText());
            }
            run(input.getText());
            input.clear();
        });

        input.setOnKeyPressed(e -> handleKey(Controls.keyStringify(e.getCode())));
        input.setOnKeyReleased(e -> genSuggestions());

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

        // box that holds the stuff on the bottom (input & resize area)
        VBox bottomBox = new VBox();
        bottomBox.setId("bottombox");
        bottomBox.getChildren().addAll(input, resizeContainer);

        // add everything
        historyScroll.setContent(historyBox);
        box.setTop(closeBtnBox);
        box.setCenter(historyScroll);
        box.setBottom(bottomBox);

        // autofill box
        suggestBox = new VBox();
        suggestBox.setId("suggestions");
        suggestBox.setPrefWidth(WIDTH - 25);
        suggestBox.setTranslateX(OFF_X + 10);
        suggestBox.setTranslateY(HEIGHT + OFF_Y - 17);
        suggestBox.setVisible(false);

        DraggableNode.add(box, box, suggestBox);
        ResizableNode.add(resizeArea, ResizableNode.ResizeDirection.ALL, box);
        // create a new resize handler to resize suggestions horizontally but move vertically
        ResizableNode.add(resizeArea, ResizableNode.ResizeDirection.H_DRAGV, suggestBox);

        scene.getChildren().addAll(box, suggestBox);
        scene.getStylesheets().add("styles/console.css");
    }

    /**
     * Loads a previous command into the input box, based on commandPos and commandHistory.
     */
    private static void loadCommand() {
        if (commandPos < -SUGGESTIONS.size() || commandPos > COMMAND_HISTORY.size()) {
            error("Could not load command. Position out of bounds.");
            return;
        }

        // command to be shown in the input
        String command = tempCommand;
        if (commandPos > 0) { // show from command history
            command = COMMAND_HISTORY.get(commandPos - 1);
        } else if (commandPos < 0) { // show from suggestions
            command = SUGGESTIONS.get(-commandPos - 1).getText();

            // add/remove styles to highlight the selected suggestion
            Node prev = suggestBox.lookup(".suggestion-selected");
            if (prev != null) {
                prev.getStyleClass().remove("suggestion-selected");
            }
            SUGGESTIONS.get(-commandPos - 1).getStyleClass().add("suggestion-selected");
        }
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
            if (commandPos == 0) { // if we're navigating away from the new command, save it
                tempCommand = input.getText();
            }
            commandPos++;
            if (commandPos > COMMAND_HISTORY.size()) {
                commandPos = COMMAND_HISTORY.size();
            }
            loadCommand();
        } else if (key.equals("DOWN") || key.equals("TAB")) { // go to next command
            if (commandPos == 0) {
                tempCommand = input.getText();
            }
            commandPos--;
            if (commandPos < -SUGGESTIONS.size()) {
                commandPos = -SUGGESTIONS.size();
            }
            loadCommand();
        } else {
            // reset command pos on key press so if the user changes a loaded command, it becomes the new command
            commandPos = 0;
        }
    }

    /**
     * Generates suggestions/autocomplete commands based on the currently inputed text.
     */
    private static void genSuggestions() {
        // currently inputed text
        String val = input.getText();
        if (val.length() > 0 && commandPos == 0) {
            SUGGESTIONS.clear();
            // search for commands starting with the text
            for (Command c : COMMANDS) {
                if (c.getName().startsWith(val.toLowerCase())) {
                    // create a new label and add it to the suggestions list
                    Label temp = new Label(c.getName());
                    SUGGESTIONS.add(temp);
                    if (SUGGESTIONS.size() == SUGG_MAX_SIZE) {
                        break;
                    }
                }
            }
            // show suggestions if more than 1
            if (SUGGESTIONS.size() > 0) {
                suggestBox.getChildren().clear();
                suggestBox.getChildren().addAll(SUGGESTIONS);
                suggestBox.setVisible(true);
            } else if (suggestBox.isVisible()) {
                suggestBox.setVisible(false);
            }
        } else if (commandPos >= 0) { // if loading a previous command, don't suggest things
            SUGGESTIONS.clear();
            suggestBox.getChildren().clear();
            if (suggestBox.isVisible()) {
                suggestBox.setVisible(false);
            }
        }
    }
}
