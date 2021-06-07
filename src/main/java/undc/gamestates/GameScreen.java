package undc.gamestates;

import javafx.animation.*;
import javafx.application.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.util.*;
import undc.controllers.*;
import undc.handlers.*;
import undc.objects.*;


import java.util.ArrayList;

public class GameScreen extends GameState {
    private static GameScreen instance;
    private static final int SANDBOX_WIDTH = 1000;
    private static final int SANDBOX_HEIGHT = 1000;

    private Player player;
    private DungeonLayout dungeonLayout;
    private Room previous;
    private Room room;
    private StackPane hud;
    private Canvas canvas;
    private StackPane inventory;
    private StackPane pause;
    private StackPane challenge;
    private boolean consoleOpen;
    private GameMode mode;

    private boolean inventoryVisible;

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

            Room start = new Room(SANDBOX_HEIGHT,SANDBOX_WIDTH, (int) ((SANDBOX_WIDTH - Vars.i("sv_player_width")) / 2.0),
                    (int) (SANDBOX_HEIGHT / 2.0 - Vars.i("sv_player_height")), RoomType.STARTROOM);
            start.setMonsters(new ArrayList<>());

            Room exit = new Room(10, 10, 0, 0, RoomType.EXITROOM);
            exit.setMonsters(new ArrayList<>());

            Room[][] arr = new Room[][]{new Room[]{start, exit}};
            dungeonLayout = new DungeonLayout(start, exit, arr);
        } else if (mode == GameMode.STORY) {
            dungeonLayout = new LayoutGenerator().generateLayout();
        }
        scene = new Scene(new Pane(), this.width, this.height);
        canvas = new Canvas();
        inventoryVisible = false;
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
        root.getChildren().addAll(roomPane, hud);
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
        root.getChildren().addAll(roomPane, hud);
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
        createHud();
        updateInventory();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().set(1, hud);
    }

    private void createPlayer() {
        player = new Player(Vars.i("sv_player_health"), 1, Controller.getDataManager().getWeapon());
        player.setDirection(3);
        getGame().setPlayer(player);
    }

    private void createHud() {
        hud = new StackPane();
        BorderPane display = new BorderPane();
        HBox lowerHUD = new HBox(100);
        display.setBottom(lowerHUD);
        lowerHUD.setAlignment(Pos.CENTER);
        lowerHUD.setPadding(new Insets(0, 150, 15, 10));

        // upper hud, includes effect indicator
        HBox upperHUD = new HBox(100);
        upperHUD.setPadding(new Insets(15, 15, 15, 15));
        display.setTop(upperHUD);
        upperHUD.setAlignment(Pos.CENTER_RIGHT);

        for (Effect e : player.getEffects()) {
            if (e.getType().equals(EffectType.ATTACKBOOST)) {
                Image attackBoostImg = new Image("effects/attackboost.png");
                ImageView attackBoost = new ImageView(attackBoostImg);
                attackBoost.setFitWidth(attackBoostImg.getWidth() * Vars.d("gc_effect_scale"));
                attackBoost.setFitHeight(attackBoostImg.getHeight() * Vars.d("gc_effect_scale"));
                upperHUD.getChildren().add(attackBoost);
            }
        }

        // lower hud labels (gold, health)
        Label goldLabel = new Label("Gold: " + player.getGold());
        Label healthLabel = new Label("Health: ");
        Label healthNumber = new Label(" " + player.getHealth() + " / " + "100.0");

        goldLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthNumber.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");

        // health bar
        Rectangle healthBarTop = new Rectangle(player.getHealth() / player.getMaxHealth() * 150, 20);
        healthBarTop.setFill(Color.LIMEGREEN);
        Rectangle healthBarBottom = new Rectangle(150, 20);
        healthBarBottom.setFill(Color.GRAY);
        StackPane healthBar = new StackPane(healthBarBottom, healthBarTop, healthNumber);
        healthBar.setAlignment(Pos.CENTER_LEFT);
        HBox healthBox = new HBox(healthLabel, healthBar);
        healthBox.setSpacing(5);
        healthBox.setAlignment(Pos.CENTER);

        boolean hasRangedWeapon = false;

        // hotbar
        HBox hotbar = new HBox(10);
        for (int i = 0; i < player.getInventory().getItems()[0].length; i++) {
            StackPane newSlot = new StackPane();
            Rectangle rect = new Rectangle(40, 40, Color.GRAY);
            if (i == player.getSelected()) {
                rect = new Rectangle(50, 50, Color.GRAY);
            }
            rect.setStyle("-fx-stroke: white; -fx-stroke-width: 1");
            hotbar.getChildren().add(newSlot);
            newSlot.getChildren().add(rect);

            // if item slot is not empty
            if (player.getInventory().getItems()[0][i] != null) {
                InventoryItem item = player.getInventory().getItems()[0][i];
                ImageView itemImg = new ImageView(item.getItem().getSprite());
                itemImg.setFitHeight(i == player.getSelected() ? 40 : 30);
                itemImg.setFitWidth(i == player.getSelected() ? 40 : 30);

                // show item quantity if > 1
                if (item.getQuantity() > 1) {
                    Label quantity = new Label("" + item.getQuantity());
                    StackPane quantityPane = new StackPane();
                    quantityPane.setAlignment(Pos.BOTTOM_RIGHT);
                    quantity.setStyle(
                            "-fx-text-fill:WHITE; -fx-font-size: 14; -fx-font-family:VT323;");
                    quantity.setTranslateX(i == player.getSelected() ? -2 : -3);
                    quantity.setTranslateY(i == player.getSelected() ? -1 : -6);
                    quantityPane.getChildren().add(quantity);
                    newSlot.getChildren().addAll(itemImg, quantityPane);
                } else {
                    newSlot.getChildren().add(itemImg);
                }
            }
        }

        // ammo label
        Item item = player.getItemSelected() != null
                ? player.getItemSelected().getItem() : null;
        if (item instanceof RangedWeapon) {
            RangedWeapon w = (RangedWeapon) item;
            String text = "Ammo: " + w.getAmmo().getRemaining() + " / "
                    + w.getAmmo().getBackupRemaining();
            if (w.isReloading()) {
                text = "Ammo: Reloading";
            }
            Label ammoLabel = new Label(text);
            ammoLabel.setMinWidth(200);
            ammoLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
            lowerHUD.getChildren().addAll(healthBox, hotbar, goldLabel, ammoLabel);

            ammoLabel.setTranslateX(-50);
            lowerHUD.setTranslateX(150);
        } else {
            lowerHUD.getChildren().addAll(healthBox, hotbar, goldLabel);
        }

        hud.getChildren().add(display);
    }

    public DungeonLayout getLayout() {
        //For testing purposes
        return this.dungeonLayout;
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
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

        StackPane root = new StackPane();
        VBox box = new VBox(40);

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
        hud.getChildren().add(root);
        partialFadeIn(backdrop);
    }

    public void createPauseMenu() {
        pause = new StackPane();
        VBox box = new VBox(40);

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
        hud.getChildren().add(pause);
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
        createPlayer();

        //go to starting room
        getGame().start(dungeonLayout.getStartingRoom());
    }

    public void updateInventory() {
        inventory = new StackPane();
        VBox box = new VBox(50);
        VBox itemRows = new VBox(30);
        HBox[] itemSlots = new HBox[player.getInventory().getCols()];
        StackPane allItemLabels = new StackPane();

        ArrayList<Label> itemNameList = new ArrayList<>();

        for (int i = 0; i < itemSlots.length; i++) {
            itemSlots[i] = new HBox(30);
            itemSlots[i].setAlignment(Pos.CENTER);
        }
        for (int i = 0; i < player.getInventory().getItems().length; i++) {
            itemRows.getChildren().add(itemSlots[i]);
            for (int j = 0; j < player.getInventory().getItems()[i].length; j++) {
                StackPane newSlot = new StackPane();
                Rectangle rect = new Rectangle(75, 75, Color.GRAY);
                rect.setStyle("-fx-stroke: white; -fx-stroke-width: 3");
                itemSlots[i].getChildren().add(newSlot);
                newSlot.getChildren().add(rect);
                InventoryItem item = player.getInventory().getItems()[i][j];
                if (item != null) {
                    ImageView itemImg = new ImageView(item.getItem().getSprite());
                    itemImg.setFitWidth(60);
                    itemImg.setPreserveRatio(true);
                    Label nameLabel = new Label(item.getItem().getName());

                    if (item.getItem() instanceof RangedWeapon) {
                        RangedWeapon w = (RangedWeapon) item.getItem();
                        nameLabel.setText(w.getName() + "\n"
                                + w.getAmmo().getProjectile().getName() + " ("
                                + w.getAmmo().getRemaining() + " / "
                                + w.getAmmo().getBackupRemaining() + ")");
                    } else if (item.getItem() instanceof Weapon) {
                        Weapon w = (Weapon) item.getItem();
                        nameLabel.setText(w.getName() + "\nDamage: "
                                + (int) w.getDamage() + "\nSpeed: "
                                + (int) w.getAttackSpeed());
                    } else if (item.getItem() instanceof Potion) {
                        Potion p = (Potion) item.getItem();
                        if (p.getType().equals(PotionType.HEALTH)) {
                            nameLabel.setText(p.getName() + "\n("
                                    + (int) p.getModifier() + " HP)");
                        }
                    } else if (item.getItem() instanceof Bomb) {
                        Bomb b = (Bomb) item.getItem();
                        nameLabel.setText(b.getName() + "\nDamage: "
                                + (int) b.getDamage() + "\nRadius: "
                                + (int) b.getRadius() + "\nFuse Time: "
                                + (int) b.getFuse() / 1000 + "s");
                    }
                    nameLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; "
                            + "-fx-font-family:VT323; -fx-background-color: black; "
                            + "-fx-border-color: white; -fx-padding: 5px");
                    nameLabel.setTextAlignment(TextAlignment.CENTER);
                    nameLabel.setAlignment(Pos.TOP_CENTER);
                    nameLabel.setVisible(false);

                    newSlot.setOnMouseEntered(event -> nameLabel.setVisible(true));
                    newSlot.setOnMouseExited(event -> nameLabel.setVisible(false));

                    itemNameList.add(nameLabel);

                    if (player.getInventory().getItems()[i][j].getQuantity() > 1) {
                        Label quantity = new Label("" + item.getQuantity());
                        StackPane quantityPane = new StackPane();
                        quantityPane.setAlignment(Pos.BOTTOM_RIGHT);
                        quantity.setStyle(
                                "-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
                        quantity.setTranslateX(-4);
                        quantity.setTranslateY(-2);
                        quantityPane.getChildren().add(quantity);
                        newSlot.getChildren().addAll(itemImg, quantityPane);
                    } else {
                        newSlot.getChildren().add(itemImg);
                    }
                }
            }
        }

        Label invLabel = new Label("INVENTORY");
        invLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        inventory.setAlignment(Pos.CENTER);
        itemRows.setAlignment(Pos.CENTER);
        box.setAlignment(Pos.CENTER);

        inventoryVisible = false;
        inventory.setVisible(false);

        for (Label label : itemNameList) {
            allItemLabels.getChildren().add(label);
        }

        box.getChildren().addAll(invLabel, itemRows, allItemLabels);

        inventory.getChildren().addAll(backdrop, box);
        hud.getChildren().add(inventory);
        partialFadeIn(backdrop);
    }

    public void toggleInventory() {
        if (!inventoryVisible) {
            updateInventory();
        }
        inventoryVisible = !inventoryVisible;
        inventory.setVisible(inventoryVisible);
    }

    public void createChallengeOverlay() {
        challenge = new StackPane();
        VBox box = new VBox(40);

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
        hud.getChildren().add(challenge);
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