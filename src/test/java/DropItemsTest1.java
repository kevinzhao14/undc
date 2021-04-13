import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.objects.DroppedItem;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

import java.util.Random;

import static org.junit.Assert.assertTrue;


public class DropItemsTest1 extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Before
    public void setUp() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl robot = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        robot.write("test");

        //randomly select difficulty to ensure that player has default gold after restart
        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        Random r = new Random();
        clickOn(difficulties[r.nextInt(difficulties.length)]);

        clickOn("Sword");
        clickOn("Next");
    }

    @Test
    public void testValidSpawnPoint() {
        Monster testMonster = null;
        double roomWidth = ((GameScreen) Controller.getState()).getRoom().getWidth();
        double roomHeight = ((GameScreen) Controller.getState()).getRoom().getHeight();

        Room[][] grid = ((GameScreen) Controller.getState()).getLayout().getGrid();

        //Search for a room with at least one monster
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                Room r = grid[i][j];
                if (r != null && r.getMonsters() != null
                        && r.getMonsters().length > 0) {
                    testMonster = r.getMonsters()[0];
                    roomWidth = ((GameScreen) Controller.getState()).getRoom().getWidth();
                    roomHeight = ((GameScreen) Controller.getState()).getRoom().getHeight();
                    break;
                }
            }
        }

        if (testMonster != null) {
            DroppedItem[] droppedItems = testMonster.dropItems();
            boolean validDropAmt = droppedItems.length >= GameSettings.MIN_ITEM_DROP
                    && droppedItems.length <= GameSettings.MAX_ITEM_DROP;
            assertTrue(validDropAmt);

            for (DroppedItem item : droppedItems) {
                boolean validDropLocation = item.getX() > 0.0 && item.getX() < roomWidth
                        && item.getY() > 0.0 && item.getY() < roomHeight;
                assertTrue(validDropLocation);
            }
        }

    }
}
