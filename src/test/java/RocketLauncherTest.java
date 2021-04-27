import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.handlers.LayoutGenerator;
import dungeoncrawler.objects.Ammunition;
import dungeoncrawler.objects.ChallengeRoom;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Inventory;
import dungeoncrawler.objects.InventoryItem;
import dungeoncrawler.objects.Item;
import dungeoncrawler.objects.RangedWeapon;
import dungeoncrawler.objects.Room;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import static org.junit.Assert.assertTrue;

public class RocketLauncherTest extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void itemsTest() {
        Item[] items = DataManager.ITEMS;
        boolean rocketLauncherExists = false;
        boolean ammoExists = false;
        for (Item i : items) {
            if (i instanceof RangedWeapon && i.getName().equals("Rocket Launcher")) {
                rocketLauncherExists = true;
            }
            if (i instanceof Ammunition && i.getName().equals("Rockets")) {
                ammoExists = true;
            }
        }
        assertTrue(rocketLauncherExists);
        assertTrue(ammoExists);
    }

    @Test
    public void testDropAvailable() {
        boolean canDropRocketLauncher = false;
        //get invs
        DungeonLayout layout = new LayoutGenerator().generateLayout();
        for (Room[] rooms : layout.getGrid()) {
            for (Room r : rooms) {
                if (r instanceof ChallengeRoom) {
                    Inventory inv = ((ChallengeRoom) r).getRewards();
                    for (InventoryItem i : inv.getItemsList()) {
                        if (i.getItem() instanceof RangedWeapon
                                && i.getItem().getName().equals("Rocket Launcher")) {
                            canDropRocketLauncher = true;
                        }
                    }
                }
            }
        }
        assertTrue(canDropRocketLauncher);
    }
}
