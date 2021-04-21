import dungeoncrawler.controllers.Controller;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;


public class MonsterFadeOnDeathTest extends ApplicationTest {
    private Controller c;
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testLivingMonsterIsVisible() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] != null && roomGrid[i][j].getMonsters() != null) {
                    Monster[] monsters = roomGrid[i][j].getMonsters();
                    for (Monster monster : monsters) {
                        if (monster == null) {
                            continue;
                        }
                        assertEquals(1, monster.getOpacity(), .00001);
                    }
                }
            }
        }
    }

    @Test
    public void testMonsterDeathProgressBeginsAt1() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] != null && roomGrid[i][j].getMonsters() != null) {
                    Monster[] monsters = roomGrid[i][j].getMonsters();
                    for (Monster monster : monsters) {
                        if (monster == null) {
                            continue;
                        }
                        assertEquals(1, monster.getOpacity(), .00001);
                    }
                }
            }
        }
    }
}
