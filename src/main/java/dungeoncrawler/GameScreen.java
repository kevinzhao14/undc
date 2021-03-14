package dungeoncrawler;


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

public class GameScreen extends GameState {

    private GameController game;
    private ImageView player;
    private DungeonLayout dungeonLayout;
    private Room room;
    private BorderPane hud;

    public GameScreen(int width, int height) {
        player = new ImageView("player-down.png");
        player.setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU);
        player.setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);

        /*
        //
        Room tempRoom2 = new Room(300, 300, 50, 50, new Obstacle[5], RoomType.EMPTYROOM);
        Door tempDoor = new Door(200, 300, 20, 10, tempRoom2, DoorOrientation.TOP);
        Room tempRoom = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        tempRoom.setTopDoor(tempDoor);
        */

        dungeonLayout = LayoutGenerator.generateLayout();
        room = dungeonLayout.getStartingRoom();

        scene = new Scene(new Pane(), width, height);
        //setRoom(room);
    }

    public void start() {
        game = new GameController(player);
        game.start(room);
    }

    public boolean setRoom(Room newRoom) {
        player = new ImageView("player-down.png");
        player.setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU);
        player.setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);
        game.setPlayer(player);
        room = newRoom;
        StackPane root = new StackPane();
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
        boolean gameWon = false;
        if (newRoom.equals(dungeonLayout.getExitRoom())) {
            //yay we won
            VBox box = new VBox();
            Label winnerLabel = new Label("Congratulations! You have escaped from the dungeon!");
            Button endButton = new Button("Exit Game");
            endButton.setOnAction((e) -> {
                Platform.exit();
            });
            box.getChildren().addAll(winnerLabel, endButton);
            box.setAlignment(Pos.CENTER);
            root.getChildren().addAll(hud, box);
            gameWon = true;
        } else {
            root.getChildren().addAll(RoomRenderer.drawRoom(scene, room, player), hud);
        }
        scene.setRoot(root);

        return gameWon;
    }






}
