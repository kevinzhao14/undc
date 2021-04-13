package dungeoncrawler.gamestates;

import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Room;
import dungeoncrawler.handlers.RoomRenderer;
import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.GameController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javafx.scene.image.ImageView;


public class GameScreen extends GameState {

    private GameController game;
    private Player player;
    private DungeonLayout dungeonLayout;
    private Room room;
    private StackPane hud;
    private Canvas canvas;
    private StackPane inventory;
    private StackPane pause;
    private boolean inventoryVisible;
    private boolean paused;
    private int currentSelectedItem;

    public GameScreen(int width, int height) {
        dungeonLayout = LayoutGenerator.generateLayout();
        scene = new Scene(new Pane(), width, height);
        canvas = new Canvas();
        inventoryVisible = false;
        paused = false;
        currentSelectedItem = 0;
    }

    public void start() {
        game = new GameController();
        createPlayer();
        switch (Controller.getDataManager().getDifficulty()) {
        case EASY:
            player.setGold(300);
            break;
        case MEDIUM:
            player.setGold(200);
            break;
        default:
            player.setGold(100);
            break;
        }
        game.start(dungeonLayout.getStartingRoom());
        scene.getStylesheets().add("http://fonts.googleapis.com/css?family=VT323");
    }

    public boolean setRoom(Room newRoom) {
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

    private void createRoom() {
        //set new room
        StackPane root = new StackPane();

        //create player and hud
        game.resetPos();
        createHud();
        updateInventory();
        createPauseMenu();

        //if won, set scene as win screen
        if (room.equals(dungeonLayout.getExitRoom())) {
            game.pause();
            hud.setVisible(false);

            VBox box = new VBox(40);
            Label winnerLabel = new Label("Congratulations! You have escaped from the dungeon!");
            winnerLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

            Button endButton = new Button("Exit Game");
            endButton.setMinWidth(600);
            endButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

            endButton.setOnAction((e) -> {
                Platform.exit();
            });

            box.getChildren().addAll(winnerLabel, endButton);
            box.setAlignment(Pos.CENTER);
            root.getChildren().addAll(box);
            fadeIn(box);
        } else {
            Pane roomPane = RoomRenderer.drawRoom(scene, room, canvas);
            root.getChildren().addAll(roomPane, hud);
            if (scene.getRoot().getChildrenUnmodifiable().size() > 0) {
                fadeIn(roomPane);
            } else {
                game.updateRoom();
            }
        }
        root.setStyle("-fx-background-color: #34311b");
        scene.setRoot(root);
    }

    public void updateHud() {
        createHud();
        updateInventory();
        createPauseMenu();
        Pane root = (Pane) scene.getRoot();
        root.getChildren().set(1, hud);
    }

    private void createPlayer() {
        player = new Player(GameSettings.PLAYER_HEALTH, 1,
                Controller.getDataManager().getWeapon());
        player.setDirection(3);
        game.setPlayer(player);
    }

    private void createHud() {
        hud = new StackPane();
        BorderPane display = new BorderPane();
        HBox lowerHUD = new HBox(100);
        display.setBottom(lowerHUD);
        lowerHUD.setAlignment(Pos.CENTER);
        lowerHUD.setPadding(new Insets(0, 150, 15, 10));

        Label goldLabel = new Label("Gold: " + player.getGold());
        Label healthLabel = new Label("Health: ");
        Label healthNumber = new Label(" " + player.getHealth() + " / " + "100.0");

        goldLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthNumber.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");

        Rectangle healthBarTop =
                new Rectangle(player.getHealth() / player.getMaxHealth() * 150, 20);
        healthBarTop.setFill(Color.LIMEGREEN);
        Rectangle healthBarBottom = new Rectangle(150, 20);
        healthBarBottom.setFill(Color.GRAY);
        StackPane healthBar = new StackPane(healthBarBottom, healthBarTop, healthNumber);
        healthBar.setAlignment(Pos.CENTER_LEFT);
        HBox healthBox = new HBox(healthLabel, healthBar);
        healthBox.setSpacing(5);

        HBox hotbar = new HBox(10);
        for (int i = 0; i < player.getInventory().getItems()[0].length; i++) {
            StackPane newSlot = new StackPane();
            if (i == player.getItemSelected()) {
                Rectangle rect = new Rectangle(40, 40, Color.GRAY);
                rect.setStyle("-fx-stroke: white; -fx-stroke-width: 1");
                hotbar.getChildren().add(newSlot);
                newSlot.getChildren().add(rect);
                if (player.getInventory().getItems()[0][i] != null) {
                    ImageView itemImg = new ImageView(player.getInventory().getItems()[0][i].getItem().getSprite());
                    newSlot.getChildren().add(itemImg);
                }
            } else {
                Rectangle rect = new Rectangle(30, 30, Color.GRAY);
                rect.setStyle("-fx-stroke: white; -fx-stroke-width: 1");
                hotbar.getChildren().add(newSlot);
                newSlot.getChildren().add(rect);
                if (player.getInventory().getItems()[0][i] != null) {
                    ImageView itemImg = new ImageView(player.getInventory().getItems()[0][i].getItem().getSprite());
                    newSlot.getChildren().add(itemImg);
                }
            }
        }

        lowerHUD.getChildren().addAll(healthBox, hotbar, goldLabel);

        hud.getChildren().add(display);
    }


    public DungeonLayout getLayout() {
        //For testing purposes
        return this.dungeonLayout;
    }

    public boolean isInventoryVisible() {
        return inventoryVisible;
    }

    public boolean isPaused() {
        return paused;
    }

    private void fadeOut(Pane pane) {
        FadeTransition transition = new FadeTransition();
        setFade(transition, pane, false);
        transition.setOnFinished((e) -> createRoom());
    }

    private void fadeIn(Pane pane) {
        FadeTransition transition = new FadeTransition();
        setFade(transition, pane, true);
        transition.setOnFinished((e) -> game.updateRoom());
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

    public void gameOver() {
        StackPane root = new StackPane();
        VBox box = new VBox(40);

        Rectangle backdrop = new Rectangle(scene.getWidth(), scene.getHeight());
        backdrop.setFill(Color.BLACK);

        Label deathLabel = new Label("GAME OVER");
        deathLabel.setStyle("-fx-text-fill: white; -fx-font-family:VT323; -fx-font-size:50");

        Button restartButton = new Button("Restart");
        Button endButton = new Button("Exit Game");

        restartButton.setMinWidth(600);
        endButton.setMinWidth(600);

        restartButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");
        endButton.setStyle("-fx-font-family:VT323; -fx-font-size:25");

        restartButton.setOnAction((e) -> {
            restartGame();
        });
        endButton.setOnAction((e) -> {
            Platform.exit();
        });

        box.getChildren().addAll(deathLabel, restartButton, endButton);
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
            game.pause();
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
        paused = !paused;
        pause.setVisible(paused);
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
                    for (Monster m : room.getMonsters()) {
                        if (m != null) {
                            int monsterX = (int) (Math.random() * (room.getWidth() - 39)) + 20;
                            int monsterY = (int) (Math.random() * (room.getHeight() - 39)) + 20;
                            m.setPosX(monsterX);
                            m.setPosY(monsterY);
                            m.setHealth(m.getMaxHealth());
                        }
                    }
                }
            }
        }
        //set player health to original amt
        player.setHealth(player.getMaxHealth());

        //set player gold value to original amt - MAKE SURE TO UNCOMMENT LINES BELOW
        switch (Controller.getDataManager().getDifficulty()) {
        case EASY:
            player.setGold(300);
            break;
        case MEDIUM:
            player.setGold(200);
            break;
        default:
            player.setGold(100);
            break;
        }

        //go to starting room
        setRoom(dungeonLayout.getStartingRoom());
    }

    public void updateInventory() {
        inventory = new StackPane();
        VBox box = new VBox(50);
        VBox itemRows = new VBox(30);
        HBox[] itemSlots = new HBox[GameSettings.INVENTORY_COLUMNS];
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
                if (player.getInventory().getItems()[i][j] != null) {
                    ImageView itemImg = new ImageView(player.getInventory().getItems()[i][j].getItem().getSprite());
                    newSlot.getChildren().add(itemImg);
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

        box.getChildren().addAll(invLabel, itemRows);

        inventory.getChildren().addAll(backdrop, box);
        hud.getChildren().add(inventory);
        partialFadeIn(backdrop);
    }

    public void toggleInventory() {
        if (!paused) {
            inventoryVisible = !inventoryVisible;
            System.out.println(inventoryVisible);
            inventory.setVisible(inventoryVisible);
        }
    }

    public void refresh() {
        RoomRenderer.drawFrame(canvas, room, player);
    }

    // for testing
    public Player getPlayer() {
        return player;
    }

    public Canvas getCanvas() {
        return canvas;
    }
    public GameController getGame() {
        return game;
    }
    public Room getRoom() {
        return room;
    }
}
