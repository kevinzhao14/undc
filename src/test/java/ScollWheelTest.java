import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import javafx.geometry.VerticalDirection;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

public class ScollWheelTest extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testSelectedItemWrapsOnScroll() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        GameScreen gameScreen = (GameScreen) Controller.getState();
        Assert.assertEquals(gameScreen
                .getPlayer().getItemSelected().getItem().getName(), "Sword");

        // make sure we wrap selected item
        gameScreen.getPlayer().moveLeft();
        Assert.assertNull(gameScreen.getPlayer().getItemSelected());
    }

    @Test
    public void testHandleScrollEvent() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        GameScreen gameScreen = (GameScreen) Controller.getState();
        Assert.assertEquals(gameScreen
                .getPlayer().getItemSelected().getItem().getName(), "Sword");

        // try scrolling away from the currently selected sword
        scroll(VerticalDirection.DOWN);
        Assert.assertNull(gameScreen.getPlayer().getItemSelected());
    }
}
