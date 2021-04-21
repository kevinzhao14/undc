import dungeoncrawler.controllers.Controller;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.MonsterType;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MonsterGenerationTest extends ApplicationTest {
    private Controller c;
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }
    @Test
    public void testMonsterNumber() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] != null) {
                    assertTrue((roomGrid[i][j].getMonsters().length >= GameSettings.MIN_MONSTERS)
                            && (roomGrid[i][j].getMonsters().length <= GameSettings.MAX_MONSTERS));
                }
            }
        }
    }

    @Test
    public void testMonsterType() {
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        Room[][] roomGrid = layout.getGrid();
        for (int i = 0; i < roomGrid.length; i++) {
            for (int j = 0; j < roomGrid[i].length; j++) {
                if (roomGrid[i][j] != null) {
                    if (roomGrid[i][j].getMonsters() != null) {
                        Monster[] monsters = roomGrid[i][j].getMonsters();
                        for (Monster monster : monsters) {
                            if (monster != null && monster.getType() == MonsterType.FAST) {
                                assertEquals(monster.getNode()
                                                .substring(monster.getNode().length() - 26),
                                        "/monsters/monster-fast.png");
                            } else if (monster != null && monster.getType() == MonsterType.TANK) {
                                assertEquals(monster.getNode()
                                                .substring(monster.getNode().length() - 26),
                                        "/monsters/monster-tank.png");
                            } else if (monster != null && monster.getType() == MonsterType.NORMAL) {
                                assertEquals(monster.getNode()
                                                .substring(monster.getNode().length() - 28),
                                        "/monsters/monster-normal.png");
                            }
                        }
                    }
                }
            }
        }
    }
}
