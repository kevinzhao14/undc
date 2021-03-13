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

    public GameScreen(int width, int height) {
        player = new ImageView("playerPlaceholder.png");
        //TODO: Implement layoutgenerator
        Room tempRoom2 = new Room(300, 300, new Obstacle[5], RoomType.EMPTYROOM);
        Door tempDoor = new Door(200, 300, 20, 10, tempRoom2, DoorOrientation.TOP);
        Room tempRoom = new Room(400, 400, new Obstacle[5], RoomType.EMPTYROOM);
        tempRoom.setTopDoor(tempDoor);

        dungeonLayout = new DungeonLayout(tempRoom, tempRoom2); //LayoutGenerator.generateLayout();
        room = dungeonLayout.getStartingRoom();

        //make scene
        StackPane root = new StackPane();
        Pane hudPane = new Pane();
        this.room = room;
        root.getChildren().addAll(hudPane, RoomRenderer.drawRoom(room, player));
        scene = new Scene(root, width, height);
    }

    public void start() {
        game = new GameController(player);
        game.start(room);
    }






}
