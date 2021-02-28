package dungeoncrawler;

import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;

public class FirstRoom extends GameState {


    private int width;
    private int height;
    private int gold;

    // treasure
    // monsters


    public FirstRoom() {
    }

    public FirstRoom(int width, int height) {
        this.width = width;
        this.height = height;

        // initialize treasure/monsters

        /*
        switch(DataManager.getDifficulty()) {
            case EASY:
                gold = 300;
                break;
            case MEDIUM:
                gold = 200;
                break;
            default:
                gold = 100;
                break;
        }

         */

        StackPane stack = new StackPane();
        this.scene = new Scene(stack, width, height);
        Label goldLabel = new Label("Gold: " + gold);
        Label roomLabel = new Label("ROOM 1");
        GridPane layout = new GridPane();
        stack.getChildren().add(layout);
        for (int i = 0; i < width / 80; i++) {
            layout.getColumnConstraints().add(new ColumnConstraints(80));
        }
        for (int j = 0; j < height / 80; j++) {
            layout.getRowConstraints().add(new RowConstraints(80));
        }
        layout.add(goldLabel, 15, 0);
        layout.add(roomLabel, 0, 0);
        layout.add(new Label("Exit 1"), 8, 1);
        layout.add(new Label("Exit 2"), 1, 5);
        layout.add(new Label("Exit 3"), 8, 8);
        layout.add(new Label("Exit 4"), 15, 5);

    }

    public Scene getScene() {
        return super.getScene();
    }

}
