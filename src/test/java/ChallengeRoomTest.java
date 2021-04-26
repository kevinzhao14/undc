import dungeoncrawler.controllers.Controller;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.ChallengeRoom;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;

public class ChallengeRoomTest extends ApplicationTest {
    private Controller c;
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testChallengeRoomGenerated() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        int numChallengeRooms = 0;
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] instanceof ChallengeRoom) {
                    numChallengeRooms++;
                }
            }
        }
        assertEquals(numChallengeRooms, 2);
    }

    @Test
    public void testChallengeRoomProperties() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] instanceof ChallengeRoom) {
                    ChallengeRoom c = (ChallengeRoom) roomGrid[i][j];
                    assertEquals(c.getMonsters().length, 5);
                    assertEquals(c.getRewards().getRows(), 2);
                    assertEquals(c.getRewards().getColumns(), 5);
                }
            }
        }
    }
}
