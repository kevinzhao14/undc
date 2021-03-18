import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.*;
import org.testfx.service.finder.impl.WindowFinderImpl;

public class RoomTest extends ApplicationTest {

    private Controller testController;

    @Override
    public void start(Stage stage) {
        testController = new Controller();
        testController.start(stage);
    }

    @Test
    public void testHasRoomType() {
        /*
        Check if every Room in DungeonLayout has an assigned RoomType
         */
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("manas");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        GameScreen currentState = (GameScreen) testController.getState();
        assertNotNull(currentState);

        DungeonLayout currentLayout = currentState.getLayout();
        assertNotNull(currentLayout);

        Room[][] currentGrid = currentLayout.getGrid();
        assertNotNull(currentGrid);

        //Check if every Room object in grid has an assigned RoomType
        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
                if (currentGrid[i][j] != null) {
                    assertNotNull(currentGrid[i][j].getType());
                }
            }
        }
    }

    @Test
    public void testIsReachableRoom() {
        /*
        Check if every Room in DungeonLayout can be reached by player
         */
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("manas");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        GameScreen currentState = (GameScreen) testController.getState();
        assertNotNull(currentState);

        DungeonLayout currentLayout = currentState.getLayout();
        assertNotNull(currentLayout);

        Room[][] currentGrid = currentLayout.getGrid();
        assertNotNull(currentGrid);

        Room currentRoom;
        boolean hasDoor;

        //Check if every Room object in grid has at least non-null Door
        for (int i = 0; i < currentGrid.length; i++) {
            for (int j = 0; j < currentGrid[i].length; j++) {
                currentRoom = currentGrid[i][j];
                if (currentRoom != null) {
                    hasDoor = (currentRoom.getTopDoor() != null)
                            || (currentRoom.getBottomDoor() != null)
                            || (currentRoom.getLeftDoor() != null)
                            || (currentRoom.getRightDoor() != null);
                    assertTrue(hasDoor);
                }
            }
        }
    }
}
