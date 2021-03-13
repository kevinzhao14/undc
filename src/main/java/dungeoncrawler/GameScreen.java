package dungeoncrawler;


import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameScreen extends GameState {

    public void start() {
        Stage primaryStage = new Stage();
        ImageView playerImage = new ImageView(new Image("/resources/playerPlaceholder.png"));
        DungeonLayout dungeonLayout = LayoutGenerator.generateLayout();
        StackPane stack = new StackPane();
        this.scene = new Scene(stack);
        stack.getChildren().add(RoomRenderer.drawRoom(dungeonLayout.getStartingRoom(), playerImage));

        Controller controller = new Controller();
        controller.start(primaryStage);
    }






}
