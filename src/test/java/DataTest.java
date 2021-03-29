import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Weapon;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DataTest extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testWeapons() {
        Weapon[] weapons = DataManager.WEAPONS;
        assertEquals(3, weapons.length);
        assertNotEquals(weapons[0], weapons[1]);
        assertNotEquals(weapons[0], weapons[2]);
        assertNotEquals(weapons[1], weapons[2]);
    }

    @Test
    public void testMonsters() {
        Monster[] monsters = DataManager.MONSTERS;
        assertEquals(3, monsters.length);
        assertNotEquals(monsters[0], monsters[1]);
        assertNotEquals(monsters[0], monsters[2]);
        assertNotEquals(monsters[1], monsters[2]);
    }
}
