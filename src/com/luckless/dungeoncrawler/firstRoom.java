import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class firstRoom extends Application {

    Label goldLabel;
    Label roomLabel;
    private int width;
    private int height;

    // treasure
    // monsters


    @Override
    public void start(Stage primaryStage) throws Exception {
        Stage mainWindow = primaryStage;
        mainWindow.setTitle("game");
        firstRoom screen = new firstRoom(1000, 500);
        mainWindow.setScene(screen.getScene());
        mainWindow.show();
    }

    public firstRoom() {}

    public firstRoom(int width, int height) {
        this.width = width;
        this.height = height;
        this.goldLabel = new Label();
        this.roomLabel = new Label();
        // initialize treasure/monsters
    }
    public Scene getScene() {

        StackPane stack = new StackPane();
        Scene scene = new Scene(stack, width, height);
        goldLabel.setText("Gold: 300");
        roomLabel.setText("ROOM 1");
        GridPane layout = new GridPane();
        stack.getChildren().add(layout);
        for (int i = 0; i < 10; i++) {
            layout.getColumnConstraints().add(new ColumnConstraints(100));
        }
        for (int j = 0; j < 25; j++) {
            layout.getRowConstraints().add(new RowConstraints(20));
        }
        layout.add(goldLabel, 9, 0);
        layout.add(roomLabel, 0, 0);
        layout.add(new Label("Exit 1"), 5, 3);
        layout.add(new Label("Exit 2"), 1, 12);
        layout.add(new Label("Exit 3"), 5, 23);
        layout.add(new Label("Exit 4"), 9, 12);

        return scene;
    }

}
