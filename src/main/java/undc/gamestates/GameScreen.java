package undc.gamestates;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.controllers.DataManager;
import undc.controllers.GameController;
import undc.handlers.Difficulty;
import undc.handlers.LayoutGenerator;
import undc.handlers.RoomRenderer;
import undc.handlers.Vars;
import undc.objects.ChallengeRoom;
import undc.objects.DungeonLayout;
import undc.objects.GraphicalInventory;
import undc.objects.Inventory;
import undc.objects.InventoryItem;
import undc.objects.Item;
import undc.objects.Monster;
import undc.objects.Player;
import undc.objects.Room;
import undc.objects.RoomType;

import java.util.ArrayList;
import java.util.Map;

public class GameScreen extends GameState {
    private static GameScreen instance;
    private static final int SANDBOX_WIDTH = 1000;
    private static final int SANDBOX_HEIGHT = 1000;

    private Player player;
    private DungeonLayout dungeonLayout;
    private Room previous;
    private Room room;
    private Hud hud;
    private Canvas canvas;
    private GraphicalInventory inventory;
    private StackPane pause;
    private StackPane challenge;
    private boolean consoleOpen;
    private GameMode mode;

    private GameScreen(int width, int height) {
        super(width, height);
    }

    public static GameScreen getInstance() {
        if (instance == null) {
            instance = new GameScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
        }
        return instance;
    }

    public void newGame(GameMode mode) {
        if (mode == GameMode.SANDBOX) {
            Controller.getDataManager().newGame("example", Difficulty.EASY, DataManager.getStartingWeapons()[0]);

            Room start = new Room(SANDBOX_HEIGHT, SANDBOX_WIDTH,
                    (int) ((SANDBOX_WIDTH - Vars.i("sv_player_width")) / 2.0),
                    (int) (SANDBOX_HEIGHT / 2.0 - Vars.i("sv_player_height")), RoomType.STARTROOM);
            start.setMonsters(new ArrayList<>());

            Room exit = new Room(10, 10, 0, 0, RoomType.EXITROOM);
            exit.setMonsters(new ArrayList<>());

            Room[][] arr = new Room[][]{new Room[]{start, exit}};
            dungeonLayout = new DungeonLayout(start, exit, arr);

            // cvars
            Vars.CHEATS = true;
            Vars.find("gm_god").setVal("true", true);
        } else if (mode == GameMode.STORY) {
            dungeonLayout = new LayoutGenerator().generateLayout();
        }
        scene = new Scene(new Pane(), this.width, this.height);
        canvas = new Canvas();
        consoleOpen = false;
        this.mode = mode;
    }

    public void start() {
        if (mode == null) {
            Console.error("Game Mode not set!");
            return;
        }
        GameController.resetInstance();
        createPlayer();
        getGame().start(dungeonLayout.getStartingRoom());
        scene.getStylesheets().add("styles/global.css");
        createHud();
        createPauseMenu();
    }

    public boolean setRoom(Room newRoom) {
        //store old room
        previous = room;

        //set new room
        room = newRoom;

        //set visited
        room.setVisited(true);

        //fade out old room
        Pane root = (Pane) scene.getRoot();
        if (root.getChildren().size() > 0) {
            fadeOut((Pane) root.getChildren().get(0));
        } else {
            createRoom();
        }
        //returns true to stop the game if player has exited
        return newRoom.equals(dungeonLayout.getExitRoom());
    }

    public void updateRoom() {
        StackPane root = new StackPane();
        Pane roomPane = RoomRenderer.drawRoom(scene, room, canvas);
        root.getChildren().addAll(roomPane, hud.getHud());
        root.setStyle("-fx-background-color: #34311b");
        scene.setRoot(root);
    }

    private void createRoom() {
        //set new room
        StackPane root = new StackPane();

        //create player and hud
        getGame().resetPos();
        if (room.getType() == RoomType.CHALLENGEROOM) {
            createChallengeOverlay();
        }

        Pane roomPane = RoomRenderer.drawRoom(scene, room, canvas);
        root.getChildren().addAll(roomPane, hud.getHud());
        root.setStyle("-fx-background-color: #34311b");
        scene.setRoot(root);
        if (scene.getRoot().getChildrenUnmodifiable().size() > 0) {
            if (room.getType() != RoomType.CHALLENGEROOM || ((ChallengeRoom) room).isCompleted()) {
                fadeIn(roomPane);
            } else {
                fadeIn(roomPane, false);
            }
        } else {
            getGame().updateRoom();
        }
        if (room.getType() == RoomType.CHALLENGEROOM && !((ChallengeRoom) room).isCompleted()) {
            onChallengeEnter();
        }
    }

    public void updateHud() {
        hud.update();
    }

    private void createPlayer() {
        player = new Player(Vars.i("sv_player_health"), 1, Controller.getDataManager().getWeapon());
        player.setDirection(3);
        getGame().setPlayer(player);

        // sandbox inventory
        if (mode == GameMode.SANDBOX) {
            int count = 0;
            for (Map.Entry<Integer, Item> i : DataManager.ITEMS.entrySet()) {
                if (i.getValue().isSpawnable()) {
                    count++;
                }
            }
            Inventory inv = new Inventory((int) Math.ceil(count / 5.0) + 1, 5);
            int row = 1;
            int col = 0;
            for (Map.Entry<Integer, Item> i : DataManager.ITEMS.entrySet()) {
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

    private void createHud() {
        hud = Hud.getInstance(player);
        createInventory();
    }

    public DungeonLayout getLayout() {
        //For testing purposes
        return this.dungeonLayout;
    }

    public boolean isInventoryVisible() {
        return inventory.isVisible();
    }

    private void fadeOut(Pane pane) {
        FadeTransition transition = new FadeTransition();
        setFade(transition, pane, false);
        transition.setOnFinished((e) -> createRoom());
    }

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

    private void partialFadeIn(Node n) {
        FadeTransition transition = new FadeTransition();
        transition.setNode(n);
        transition.setFromValue(0.1);
        transition.setToValue(0.5);
        transition.play();
    }

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

        newGameButton.setOnAction((e) -> {
            Controller.setState(HomeScreen.getInstance());
        });

        restartButton.setOnAction((e) -> {
            restartGame();
        });
        endButton.setOnAction((e) -> {
            Platform.exit();
        });

        //box.getChildren().addAll(deathLabel, restartButton, endButton);
        box.getChildren().addAll(deathLabel, monstersKilled, totalDamageDealt,
                totalItemsConsumed, newGameButton, restartButton, endButton);
        box.setAlignment(Pos.CENTER);
        root.getChildren().addAll(backdrop, box);
        hud.getHud().getChildren().add(root);
        partialFadeIn(backdrop);
    }

    public void createPauseMenu() {
        pause = new StackPane();
        final VBox box = new VBox(40);

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        Label pauseLabel = new Label("Paused");
        pauseLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        Button resumeButton = new Button("Resume");
        Button endButton = new Button("Exit Game");

        resumeButton.setMinWidth(600);
        endButton.setMinWidth(600);

        resumeButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        endButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        resumeButton.setOnAction((e) -> {
            getGame().pause();
            togglePause();
        });
        endButton.setOnAction((e) -> {
            Platform.exit();
        });

        box.getChildren().addAll(pauseLabel, resumeButton, endButton);
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
                        room.getMonsters().clear();
                    } else {
                        for (Monster m : room.getMonsters()) {
                            if (m != null) {
                                int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
                                int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
                                m.revive(monsterX, monsterY);
                            }
                        }
                    }
                }
            }
        }
        Inventory inv = player.getInventory();
        createPlayer();
        player.setInventory(inv);

        //go to starting room
        getGame().start(dungeonLayout.getStartingRoom());
    }

    public void createInventory() {
        inventory = player.getInventory().getGraphicalInventory();
        hud.getHud().getChildren().add(inventory.getRoot());
    }

    public void toggleInventory() {
        inventory.toggle();
    }

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

    public Canvas getCanvas() {
        return canvas;
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

    public enum GameMode {
        SANDBOX, STORY;
    }
}