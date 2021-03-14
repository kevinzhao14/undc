package dungeoncrawler;


import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
//        Room tempRoom2 = new Room(300, 300, 50, 50, new Obstacle[5], RoomType.EMPTYROOM);
//        Door tempDoor = new Door(200, 300, 20, 10, tempRoom2, DoorOrientation.TOP);
//        Room tempRoom = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
//        tempRoom.setTopDoor(tempDoor);

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

    private void fadeOut(Pane pane) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(pane);
        transition.setFromValue(1);
        transition.setToValue(0.25);
        transition.play();
        transition.setOnFinished((e) -> createRoom());
    }

    private void fadeIn(Pane pane) {
        FadeTransition transition = new FadeTransition();
        transition.setDuration(Duration.millis(500));
        transition.setNode(pane);
        transition.setFromValue(0.25);
        transition.setToValue(1);
        transition.play();
        transition.setOnFinished((e) -> game.updateRoom());
    }



}
