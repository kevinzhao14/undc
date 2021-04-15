import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.objects.Bomb;
import dungeoncrawler.objects.Item;
import dungeoncrawler.objects.Potion;
import dungeoncrawler.objects.PotionType;
import dungeoncrawler.objects.Weapon;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertTrue;

public class ItemDataTest extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testHasItems() {
        int size = DataManager.ITEMS.length;
        assertTrue(size > 0);
    }

    @Test
    public void testHasValidItems() {
        boolean hasHealthPotion = false;
        boolean hasAttackPotion = false;
        boolean hasWeapon = false;
        boolean hasOther = false;
        for (Item i : DataManager.ITEMS) {
            if (i instanceof Potion) {
                if (((Potion) i).getType() == PotionType.HEALTH) {
                    hasHealthPotion = true;
                } else if (((Potion) i).getType() == PotionType.ATTACK) {
                    hasAttackPotion = true;
                }
            } else if (i instanceof Weapon) {
                hasWeapon = true;
            } else if (i instanceof Bomb) {
                hasOther = true;
            }
        }

        assertTrue(hasHealthPotion);
        assertTrue(hasAttackPotion);
        assertTrue(hasWeapon);
        assertTrue(hasOther);
    }
}
