package dungeoncrawler.gamestates;


import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.Room;
import dungeoncrawler.handlers.RoomRenderer;
import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.GameController;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GameScreen extends GameState {

    private GameController game;
    private ImageView player;
    private DungeonLayout dungeonLayout;
    private Room room;
    private BorderPane hud;

    public GameScreen(int width, int height) {
        dungeonLayout = LayoutGenerator.generateLayout();
        scene = new Scene(new Pane(), width, height);
    }

    public void start() {
        game = new GameController(new ImageView());
        game.start(dungeonLayout.getStartingRoom());
    }

    public boolean setRoom(Room newRoom) {
        //set new room
        room = newRoom;

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
        createPlayer();
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
            Pane roomPane = RoomRenderer.drawRoom(scene, room, player);
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

    private void createPlayer() {
        player = new ImageView("player-down.png");
        player.setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU * 2);
        player.setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);
        game.setPlayer(player);
    }

    private void createHud() {
        hud = new BorderPane();
        Label goldLabel = new Label();
        HBox lowerHUD = new HBox();
        hud.setBottom(lowerHUD);
        lowerHUD.setAlignment(Pos.CENTER);
        lowerHUD.setPadding(new Insets(10, 10, 10, 10));
        lowerHUD.getChildren().add(goldLabel);
        switch (Controller.getDataManager().getDifficulty()) {
        case EASY:
            goldLabel.setText("Gold: 300");
            break;
        case MEDIUM:
            goldLabel.setText("Gold: 200");
            break;
        default:
            goldLabel.setText("Gold: 100");
            break;
        }
        goldLabel.setTextFill(Color.WHITE);
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



}
