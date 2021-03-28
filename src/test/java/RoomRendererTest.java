import dungeoncrawler.controllers.Controller;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

import static org.junit.Assert.assertEquals;

public class RoomRendererTest extends ApplicationTest {
    private Controller c;
    
    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testPlayer() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");
        FxAssert.verifyThat(new ImageView("player/player.png"), NodeMatchers.isNotNull());

    }

    @Test
    public void testRoomType() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");
        assertEquals(c.getState().getScene().getStylesheets().get(0), "styles/EMPTYROOM.css");
    }

}
