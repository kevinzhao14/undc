import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.TextMatchers;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;


public class HUDTest extends ApplicationTest {

    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }


    @Test
    public void testDeath() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        ((GameScreen) c.getState()).getPlayer().setHealth(0);
        FxAssert.verifyThat(new Label("GAME OVER"), NodeMatchers.isNotNull());
        Assert.assertNotNull(TextMatchers.hasText("GAME OVER"));
    }


    @Test
    public void testHealthBar() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        FxAssert.verifyThat(new Label("GAME OVER"), NodeMatchers.isNotNull());
        Assert.assertNotNull(TextMatchers.hasText("GAME OVER"));
        FxAssert.verifyThat(new Rectangle(((GameScreen) c.getState()).getPlayer().getHealth()
                / ((GameScreen) c.getState()).getPlayer().getMaxHealth() * 150, 20), NodeMatchers.isNotNull());

    }



}
