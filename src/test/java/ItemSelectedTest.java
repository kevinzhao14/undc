import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Weapon;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertTrue;

public class ItemSelectedTest extends ApplicationTest {
    @Test
    public void testItemSelectedAtStart() {
        Player player = new Player(100, 10.0,
                new Weapon("name", "weapons/axe.png", 0.0, 0.0, true));
        assertTrue(player.getSelected() == 0);
    }

    @Test
    public void testMoveItems() {
        Player player = new Player(100, 10.0,
                new Weapon("name", "weapons/axe.png", 0.0, 0.0, true));
        player.moveRight();
        assertTrue(player.getSelected() == 1);
        player.moveLeft();
        assertTrue(player.getSelected() == 0);
        player.setSelected(230);
        assertTrue(player.getSelected() == 230);
        player.setSelected(0);
        player.moveLeft();
        assertTrue(player.getSelected() == 4);
        player.setSelected(4);
        player.moveRight();
        assertTrue(player.getSelected() == 0);
    }
}
