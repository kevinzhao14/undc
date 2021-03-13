package dungeoncrawler;


import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameScreen extends GameState {

    private GameController game;
    private ImageView player;
    private DungeonLayout dungeonLayout;
    private Room room;
    private Pane hud;

    public GameScreen(int width, int height) {
        player = new ImageView("playerPlaceholder.png");
        player.setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU);
        player.setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);

        //TODO: Implement layoutgenerator
        Room tempRoom2 = new Room(300, 300, 50, 50, new Obstacle[5], RoomType.EMPTYROOM);
        Door tempDoor = new Door(200, 300, 20, 10, tempRoom2, DoorOrientation.TOP);
        Room tempRoom = new Room(400, 400, 100, 100, new Obstacle[5], RoomType.EMPTYROOM);
        tempRoom.setTopDoor(tempDoor);

        dungeonLayout = new DungeonLayout(tempRoom, tempRoom2); //LayoutGenerator.generateLayout();
        room = dungeonLayout.getStartingRoom();

        scene = new Scene(new Pane(), width, height);
        //setRoom(room);
    }

    public void start() {
        game = new GameController(player);
        game.start(room);
    }

    public void setRoom(Room newRoom) {
        player = new ImageView("playerPlaceholder.png");
        player.setFitHeight(GameSettings.PLAYER_HEIGHT * GameSettings.PPU);
        player.setFitWidth(GameSettings.PLAYER_WIDTH * GameSettings.PPU);
        game.setPlayer(player);
        room = newRoom;
        StackPane root = new StackPane();
        hud = new Pane();
        root.getChildren().addAll(hud, RoomRenderer.drawRoom(room, player));
        scene.setRoot(root);
    }






}
