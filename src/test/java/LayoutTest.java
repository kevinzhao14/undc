import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Room;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class LayoutTest {

    @Test
    public void testRandomShape() {
        DungeonLayout layout1 = new LayoutGenerator().generateLayout();
        DungeonLayout layout2 = new LayoutGenerator().generateLayout();
        Room[][] roomGrid1 = layout1.getGrid();
        Room[][] roomGrid2 = layout2.getGrid();
        boolean layoutsAreIdentical = true;
        for (int i = 0; i < roomGrid1.length; i++) {
            for (int j = 0; j < roomGrid1[i].length; j++) {
                if (roomGrid1[i][j] != null && roomGrid2[i][j] == null) {
                    layoutsAreIdentical = false;
                    break;
                } else if (roomGrid2[i][j] != null && roomGrid1[i][j] == null) {
                    layoutsAreIdentical = false;
                    break;
                }
            }
        }
        assertFalse(layoutsAreIdentical);
    }

    @Test
    public void testNumberOfRooms() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();

        int rooms = 0;

        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] != null) {
                    rooms++;
                }
            }
        }

        assertTrue(rooms >= 8);

    }



}
