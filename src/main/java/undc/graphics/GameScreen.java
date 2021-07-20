package undc.graphics;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;
import undc.command.Console;
import undc.entity.Entity;
import undc.general.Controller;
import undc.command.DataManager;
import undc.game.GameController;
import undc.general.Audio;
import undc.game.Difficulty;
import undc.game.LayoutGenerator;
import undc.command.Vars;
import undc.command.CVar;
import undc.game.ChallengeRoom;
import undc.game.calc.Direction;
import undc.game.DungeonLayout;
import undc.general.Savable;
import undc.inventory.GraphicalInventory;
import undc.inventory.Inventory;
import undc.inventory.InventoryItem;
import undc.items.Item;
import undc.entity.Monster;
import undc.entity.Player;
import undc.game.Room;
import undc.game.RoomType;

import java.util.Map;

/**
 * Class that handles the different screens the player will see throughout the game.
 */
public class GameScreen extends GameState implements Savable {
    private static GameScreen instance;

    private final RenderTimer timer;

    private boolean consoleOpen;
    private DungeonLayout dungeonLayout;
    private GameMode mode;
    private Player player;
    private Room previous;
    private Room room;

    private Canvas canvas;
    private GraphicalInventory playerInv;
    private Hud hud;
    private Pane main;
    private StackPane challenge;
    private StackPane pause;

    /**
     * Constructor for a screen.
     * @param width int width of the screen
     * @param height int height of the screen
     */
    private GameScreen(int width, int height) {
        super(width, height);
        timer = new RenderTimer();
    }

    /**
     * Acts as a singleton for GameScreen, returning its instance or making one if it doesn't currently exist.
     * @return current instance of GameScreen
     */
    public static GameScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new GameScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }

    /**
     * Creates the game mode the player chooses to play.
     * @param mode GameMode the player selected
     */
    public void newGame(GameMode mode) {
        consoleOpen = false;
        this.mode = mode;

        createPlayer();
        createHud();
        createPauseMenu();
        drawRoom();

        if (mode == GameMode.SANDBOX) {
            DataManager.getInstance().newGame("example", Difficulty.EASY, DataManager.getStartingWeapons()[0]);

            dungeonLayout = new LayoutGenerator().generateSandbox();

            // cvars
            Vars.CHEATS = true;
            CVar god = Vars.find("gm_god");
            if (god == null) {
                Console.error("God is null.");
                return;
            }
            god.setVal("true", true);
            Vars.set("sv_infinite_ammo", "true");
        } else if (mode == GameMode.STORY) {
            dungeonLayout = new LayoutGenerator().generateLayout();
        }
    }

    /**
     * Starts the game by creating it and making it visible to the player.
     */
    public void start() {
        if (mode == null) {
            Console.error("Game Mode not set!");
            return;
        }
        updateHud();
        GameController.resetInstance();
        getGame().start(dungeonLayout.getStartingRoom(), player);
    }

    public void startLoaded() {
        updateHud();
        getGame().startLoaded(room, player);
    }

    /**
     * Handles transition to a new room and updates the previous room's visited value.
     * @param newRoom Room that the player entered
     */
    public void setRoom(Room newRoom) {
        if (newRoom.equals(dungeonLayout.getExitRoom())) {
            Audio.playAudio("final_boss_music");
        } else {
            Audio.playAudio("door");
        }
        //store old room
        previous = room;

        //set new room
        room = newRoom;

        //set visited
        room.setVisited(true);

        //fade out old room
        Pane root = (Pane) scene.getRoot();
        if (root.getChildren().size() > 0) {
            fadeOut(main);
        } else {
            createRoom();
        }
    }

    /**
     * Makes a new room.
     */
    private void createRoom() {
        //create player and hud
        if (room.getType() == RoomType.CHALLENGEROOM) {
            ((ChallengeRoom) room).closeDoors();
            createChallengeOverlay();
        }

        if (scene.getRoot().getChildrenUnmodifiable().size() > 0) {
            RoomRenderer.drawFrame(canvas, room, player);
            if (room.getType() != RoomType.CHALLENGEROOM || ((ChallengeRoom) room).isCompleted()) {
                fadeIn(main);
            } else {
                fadeIn(main, false);
            }
        } else {
            getGame().updateRoom();
        }
        if (room.getType() == RoomType.CHALLENGEROOM && !((ChallengeRoom) room).isCompleted()) {
            onChallengeEnter();
        }
    }

    /**
     * Draws the room and JavaFX elements.
     */
    private void drawRoom() {
        StackPane root = new StackPane();
        canvas = new Canvas();
        main = RoomRenderer.drawRoom(canvas);
        root.getChildren().addAll(main, hud.getHud());
        root.setStyle("-fx-background-color: #34311b");
        scene.setRoot(root);
        scene.getStylesheets().add("styles/global.css");
    }

    /**
     * Loads in changes to the hud.
     */
    public void updateHud() {
        hud.update();
    }

    public Hud getHud() {
        return hud;
    }

    /**
     * Makes the player.
     */
    private void createPlayer() {
        player = new Player(Vars.i("sv_player_health"), 1, DataManager.getInstance().getWeapon());
        player.setDirection(Direction.SOUTH);

        // sandbox inventory
        if (mode == GameMode.SANDBOX) {
            int count = 0;
            for (Map.Entry<String, Item> i : DataManager.ITEMS.entrySet()) {
                if (i.getValue().isSpawnable()) {
                    count++;
                }
            }
            Inventory inv = new Inventory((int) Math.ceil(count / 5.0) + 1, 5);
            int row = 1;
            int col = 0;
            for (Map.Entry<String, Item> i : DataManager.ITEMS.entrySet()) {
                if (!i.getValue().isSpawnable()) {
                    continue;
                }
                InventoryItem item = new InventoryItem(i.getValue(), 1);
                item.setInfinite(true);
                inv.add(item, row, col);
                col++;
                if (col == 5) {
                    row++;
                    col = 0;
                }
            }
            player.setInventory(inv);
        }
    }

    /**
     * Makes the hud.
     */
    private void createHud() {
        hud = new Hud(player);
        createInventory();
    }

    public DungeonLayout getLayout() {
        //For testing purposes
        return this.dungeonLayout;
    }

    public boolean isInventoryOpen() {
        return playerInv.isVisible();
    }

    /**
     * Handles the transition between room changes.
     * @param pane Pane that will fade out of view
     */
    private void fadeOut(Pane pane) {
        FadeTransition transition = new FadeTransition();
        setFade(transition, pane, false);
        transition.setOnFinished((e) -> createRoom());
    }

    /**
     * Handles the transition between room changes and on unpausing the game.
     * @param pane Pane that will fade into view
     * @param unpause boolean for whether or not the game is unpaused
     */
    private void fadeIn(Pane pane, boolean unpause) {
        FadeTransition transition = new FadeTransition();
        setFade(transition, pane, true);
        if (unpause) {
            transition.setOnFinished((e) -> getGame().updateRoom());
        }
    }

    private void fadeIn(Pane pane) {
        fadeIn(pane, true);
    }

    /**
     * Creates the fade effect for transitions.
     * @param t FadeTransition that will occur
     * @param n Node to fade
     * @param fadeIn boolean for whether or not the node is faded
     */
    private void setFade(FadeTransition t, Node n, boolean fadeIn) {
        t.setDuration(Duration.millis(500));
        t.setNode(n);
        if (fadeIn) {
            t.setFromValue(0.25);
            t.setToValue(1);
        } else {
            t.setFromValue(1);
            t.setToValue(0.25);
        }
        t.play();
    }

    /**
     * Handles a miniature version of the fade transition.
     * @param n Node to fade
     */
    private void partialFadeIn(Node n) {
        FadeTransition transition = new FadeTransition();
        transition.setNode(n);
        transition.setFromValue(0.1);
        transition.setToValue(0.5);
        transition.play();
    }

    /**
     * Toggles the visibiility of the console.
     */
    public void toggleConsole() {
        StackPane root = (StackPane) scene.getRoot();
        if (!consoleOpen) {
            Pane console = Console.getScene();
            root.getChildren().add(console);
        } else {
            root.getChildren().remove(root.getChildren().size() - 1);
        }
        consoleOpen = !consoleOpen;
    }

    /**
     * Handles the game over screen if the player dies.
     */
    public void gameOver() {
        getGame().stop();

        final StackPane root = new StackPane();
        final VBox box = new VBox(40);

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        Label deathLabel = new Label("GAME OVER");
        deathLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        //add stats
        Label monstersKilled = new Label("Total monsters killed: "
                + getPlayer().getMonstersKilled());
        monstersKilled.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:25");
        Label totalDamageDealt = new Label("Total damage dealt: "
                + getPlayer().getTotalDamageDealt());
        totalDamageDealt.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:25");
        Label totalItemsConsumed = new Label("Total items consumed/used: "
                + getPlayer().getTotalItemsConsumed());
        totalItemsConsumed.setStyle("-fx-text-fill: white; -fx-font-family:VT323;"
                + "-fx-font-size:25");

        Button restartButton = new Button("Restart");
        Button endButton = new Button("Exit Game");

        restartButton.setMinWidth(600);
        endButton.setMinWidth(600);

        restartButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        endButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        Button newGameButton = new Button("New Game");
        newGameButton.setMinWidth(600);
        newGameButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        newGameButton.setOnAction((e) -> Controller.setState(HomeScreen.getInstance()));

        restartButton.setOnAction((e) -> restartGame());

        endButton.setOnAction((e) -> Controller.quit());

        //box.getChildren().addAll(deathLabel, restartButton, endButton);
        box.getChildren().addAll(deathLabel, monstersKilled, totalDamageDealt,
                totalItemsConsumed, newGameButton, restartButton, endButton);
        box.setAlignment(Pos.CENTER);
        root.getChildren().addAll(backdrop, box);
        hud.getHud().getChildren().add(root);
        partialFadeIn(backdrop);
    }

    /**
     * Called when the player enters the door in the boss room. Presents the victor screen.
     */
    public void win() {
        Audio.playAudio("game_win");
        final StackPane root = new StackPane();

        hud.getHud().setVisible(false);

        final VBox box = new VBox(40);
        Label winnerLabel = new Label("Congratulations! You have escaped from the dungeon!");
        winnerLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        //add stats
        Label monstersKilled = new Label("Total monsters killed: "
                + getPlayer().getMonstersKilled());
        monstersKilled.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:25");
        Label totalDamageDealt = new Label("Total damage dealt: "
                + getPlayer().getTotalDamageDealt());
        totalDamageDealt.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:25");
        Label totalItemsConsumed = new Label("Total items consumed/used: "
                + getPlayer().getTotalItemsConsumed());
        totalItemsConsumed.setStyle("-fx-text-fill: white; -fx-font-family:VT323;"
                + "-fx-font-size:25");

        Button newGameButton = new Button("New Game");
        newGameButton.setMinWidth(600);
        newGameButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        Button endButton = new Button("Exit");
        endButton.setMinWidth(600);
        endButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        newGameButton.setOnAction((e) -> {
            Audio.stopAudio();
            HomeScreen.resetInstance();
            Controller.setState(HomeScreen.getInstance());
        });

        endButton.setOnAction((e) -> {
            Audio.stopAudio();
            Controller.quit();
        });

        //box.getChildren().addAll(winnerLabel, newGameButton, endButton);
        box.getChildren().addAll(winnerLabel, monstersKilled, totalDamageDealt,
                totalItemsConsumed, newGameButton, endButton);
        box.setAlignment(Pos.CENTER);
        root.getChildren().addAll(box);
        fadeIn(box, false);

        root.setStyle("-fx-background-color: #34311b");
        scene.setRoot(root);
    }

    /**
     * Makes the pause menu graphics for when the game is paused.
     */
    public void createPauseMenu() {
        pause = new StackPane();
        final VBox box = new VBox(40);

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        Label pauseLabel = new Label("Paused");
        pauseLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        Button resumeButton = new Button("Resume");
        Button saveButton = new Button("Save Game");
        Button exitButton = new Button("Exit Game");

        resumeButton.setMinWidth(600);
        saveButton.setMinWidth(600);
        exitButton.setMinWidth(600);

        resumeButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        saveButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        exitButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        resumeButton.setOnAction((e) -> {
            getGame().pause();
            togglePause();
        });
        saveButton.setOnAction(e -> getGame().save());
        exitButton.setOnAction((e) -> {
            togglePause();
            HomeScreen.resetInstance();
            Controller.setState(HomeScreen.getInstance());
        });

        box.getChildren().addAll(pauseLabel, resumeButton, saveButton, exitButton);
        box.setAlignment(Pos.CENTER);
        pause.getChildren().addAll(backdrop, box);
        hud.getHud().getChildren().add(pause);
        partialFadeIn(backdrop);
        pause.setVisible(false);
    }

    public void togglePause() {
        pause.setVisible(!pause.isVisible());
    }

    /**
     * Returns the game to the state right after player leaves InitPlayerConfigScreen
     * and enters first room. The DungeonLayout remains the same, all visited rooms
     * become unvisited, monsters are restored to original health, and
     * player has original gold amt.
     */
    public void restartGame() {
        //set all visited values to false in Room[][] grid
        //set all monsters in visited rooms to max health
        for (Room[] roomRow : dungeonLayout.getGrid()) {
            for (Room room : roomRow) {
                if (room != null && room.wasVisited()) {
                    room.setVisited(false);
                    room.getObstacles().clear();
                    room.getDroppedItems().clear();
                    room.getProjectiles().clear();
                    if (mode == GameMode.SANDBOX) {
                        room.getEntities().clear();
                    } else {
                        for (Entity e : room.getEntities()) {
                            if (e instanceof Monster) {
                                int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
                                int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
                                ((Monster) e).revive(monsterX, monsterY);
                            }
                        }
                    }
                }
            }
        }
        Inventory inv = player.getInventory();
        createPlayer();
        player.setInventory(inv);
        createHud();
        drawRoom();

        //go to starting room
        start();
    }

    public void createInventory() {
        playerInv = player.getInventory().getGraphicalInventory();
        hud.getHud().getChildren().add(playerInv.getRoot());
    }

    public void toggleInventory() {
        playerInv.toggle();
    }

    /**
     * Adds an overlay object to the GameScreen to be toggled later.
     * @param overlay Overlay to add
     */
    public void addOverlay(Overlay overlay) {
        // check if it already exists
        for (Node n : hud.getHud().getChildren()) {
            if (n.equals(overlay.getRoot())) {
                return;
            }
        }

        Platform.runLater(() -> hud.getHud().getChildren().add(overlay.getRoot()));
    }

    /**
     * Prompts the player with the option of entering or not entering the challenge room.
     */
    public void createChallengeOverlay() {
        challenge = new StackPane();
        final VBox box = new VBox(40);

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        Label pauseLabel = new Label("You have entered a Challenge Room."
                + "Would you like to partake in the trial?");
        pauseLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:40");

        Button yesButton = new Button("Proceed");
        Button noButton = new Button("Leave");

        yesButton.setMinWidth(600);
        noButton.setMinWidth(600);

        yesButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        noButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        yesButton.setOnAction((e) -> {
            Audio.playAudio("challenge_room");
            challenge.setVisible(false);
            getGame().pause();
        });

        noButton.setOnAction((e) -> {

            int x = (int) player.getX();
            int y = (int) player.getY();

            if (x < 100 || x > room.getWidth() - player.getWidth() - 100) {
                // left/right
                previous.setStartX(room.getWidth() - room.getStartX());
                previous.setStartY(y);
            } else if (y < 50) {
                // leave through bottom
                previous.setStartY(room.getHeight() - room.getStartY() - 30);
                previous.setStartX(x);
            } else {
                // leave through top
                previous.setStartY(room.getHeight() - room.getStartY());
                previous.setStartX(x);
            }
            getGame().setRoom(previous);

            challenge.setVisible(false);
        });

        box.getChildren().addAll(pauseLabel, yesButton, noButton);
        box.setAlignment(Pos.CENTER);
        challenge.getChildren().addAll(backdrop, box);
        hud.getHud().getChildren().add(challenge);
        partialFadeIn(backdrop);
        challenge.setVisible(false);
    }

    public void onChallengeEnter() {
        challenge.setVisible(!challenge.isVisible());
    }

    // for testing
    public Player getPlayer() {
        return player;
    }

    private GameController getGame() {
        return GameController.getInstance();
    }

    public Room getRoom() {
        return room;
    }

    public boolean isConsoleOpen() {
        return consoleOpen;
    }

    public GameMode getMode() {
        return mode;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("player", player.saveObject());
        o.put("layout", dungeonLayout.saveObject());
        o.put("room", room.getId());
        o.put("mode", mode.toString());
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            mode = GameMode.valueOf(o.getString("mode"));
            newGame(mode);

            // load data
            if (!player.parseSave(o.getJSONObject("player"))) {
                return false;
            }
            if (!dungeonLayout.parseSave(o.getJSONObject("layout"))) {
                return false;
            }
            room = dungeonLayout.get(o.getInt("room"));
        } catch (Exception e) {
            Console.error("Failed to load Game data.");
            return false;
        }
        return true;
    }

    public RenderTimer getTimer() {
        return timer;
    }

    /**
     * Enumerations for the different type of game modes.
     */
    public enum GameMode {
        SANDBOX, STORY
    }

    /**
     * Represents the client's rendering process.
     */
    public class RenderTimer extends AnimationTimer {
        private long lastFrameTime;

        RenderTimer() {
        }

        @Override
        public void handle(long now) {
            RoomRenderer.drawFrame(canvas, room, player);
            long delta = now - lastFrameTime;
            lastFrameTime = now;
            double fps = 1d / delta;
            // System.out.println(fps * 1e9);
        }
    }
}