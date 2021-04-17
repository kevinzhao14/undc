import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.InventoryItem;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

public class InventoryGUITest extends ApplicationTest {

    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }


    @Test
    public void testInventoryPopup() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        press(KeyCode.I);
        FxAssert.verifyThat(new Rectangle(75, 75, Color.GRAY), NodeMatchers.isNotNull());
    }

    @Test
    public void testItemQuantity() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        press(KeyCode.I);

        InventoryItem[][] inv = ((GameScreen) c.getState()).getPlayer().getInventory().getItems();

        for (InventoryItem[] i : inv) {
            for (InventoryItem j : i) {
                if (j != null && j.getQuantity() > 1) {
                    FxAssert.verifyThat(new Label("" + j.getQuantity()), NodeMatchers.isNotNull());
                }
            }
        }
    }


}
