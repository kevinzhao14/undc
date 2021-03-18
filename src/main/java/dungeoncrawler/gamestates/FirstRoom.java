package dungeoncrawler.gamestates;

import dungeoncrawler.controllers.Controller;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Label;

/**
 * Class representation of the first level screen
 * for the Team Luckless Dungeon Crawler game.
 *
 * @author Trenton Wong
 * @version 1.0
 * @userid twong66
 */
public class FirstRoom extends GameState {


    private int width;
    private int height;
    private int gold;


    /**
     * Constructor of the first room that initializes the scene.
     *
     * @param width Horizontal width of the scene window
     * @param height Vertical height of the scene window
     */
    public FirstRoom(int width, int height) {
        this.width = width;
        this.height = height;

        // initialize treasure/monsters


        switch (Controller.getDataManager().getDifficulty()) {
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

    /**
     * Returns the FirstRoom scene
     *
     * @return the scene of the first room
     */
    public Scene getScene() {
        return super.getScene();
    }

    /**
     * Returns the starting amount of gold
     *
     * @return gold amount
     */
    public int getGold() {
        return this.gold;
    }


}
