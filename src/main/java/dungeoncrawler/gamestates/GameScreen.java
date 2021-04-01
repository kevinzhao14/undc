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
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;



public class GameScreen extends GameState {

    private GameController game;
    private Player player;
    private DungeonLayout dungeonLayout;
    private Room room;
    private StackPane hud;
    private Canvas canvas;

    public GameScreen(int width, int height) {
        dungeonLayout = LayoutGenerator.generateLayout();
        scene = new Scene(new Pane(), width, height);
        canvas = new Canvas();
    }

    public GameScreen(DungeonLayout existingDungeon, int width, int height) {
        scene = new Scene(new Pane(), width, height);
        canvas = new Canvas();
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
//        createPlayer();
        game.resetPos();
        createHud();

        //if won, set scene as win screen
        if (room.equals(dungeonLayout.getExitRoom())) {
            VBox box = new VBox();
            Label winnerLabel = new Label("Congratulations! You have escaped from the dungeon!");
            winnerLabel.setStyle("-fx-text-fill: white");
            Button endButton = new Button("Exit Game");
            endButton.setOnAction((e) -> {
                Platform.exit();
            });
            box.getChildren().addAll(winnerLabel, endButton);
            box.setAlignment(Pos.CENTER);
            root.getChildren().addAll(hud, box);
        } else {
            Pane roomPane = RoomRenderer.drawRoom(scene, room, canvas);
            root.getChildren().addAll(roomPane, hud);
            //set player position
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
        Pane root = (Pane) scene.getRoot();
        root.getChildren().set(1, hud);
    }

    private void createPlayer() {
        player = new Player(GameSettings.PLAYER_HEALTH, 1,
                Controller.getDataManager().getWeapon());
        player.setDirection(3);
//        player.getNode().setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU * 2);
//        player.getNode().setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);
        game.setPlayer(player);
    }

    private void createHud() {
        hud = new StackPane();
        BorderPane display = new BorderPane();
        HBox lowerHUD = new HBox(300);
        display.setBottom(lowerHUD);
        lowerHUD.setAlignment(Pos.CENTER);
        lowerHUD.setPadding(new Insets(0, 150, 15, 10));

        Label goldLabel = new Label("Gold: " + player.getGold());
        Label healthLabel = new Label("Health: ");
        Label healthNumber = new Label(" " + player.getHealth() + " / " + "100.0");

        goldLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthLabel.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");
        healthNumber.setStyle("-fx-text-fill:WHITE; -fx-font-size: 24; -fx-font-family:VT323");

        Rectangle healthBarTop = new Rectangle(player.getHealth() / player.getMaxHealth() * 150, 20);
        healthBarTop.setFill(Color.LIMEGREEN);
        Rectangle healthBarBottom = new Rectangle(150, 20);
        healthBarBottom.setFill(Color.GRAY);
        StackPane healthBar = new StackPane(healthBarBottom, healthBarTop, healthNumber);
        healthBar.setAlignment(Pos.CENTER_LEFT);
        HBox healthBox = new HBox(healthLabel, healthBar);
        healthBox.setSpacing(5);

        lowerHUD.getChildren().addAll(healthBox, goldLabel);

        hud.getChildren().add(display);
    }

    public DungeonLayout getLayout() {
        //For testing purposes
        return this.dungeonLayout;
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
}
