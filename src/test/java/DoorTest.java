import dungeoncrawler.controllers.Controller;
import dungeoncrawler.objects.DoorOrientation;
import dungeoncrawler.gamestates.GameScreen;
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

public class DoorTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Controller c = new Controller();
        c.start(stage);
    }

    @Test
    public void testStartRoomRendersAllDoors() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");
        FxAssert.verifyThat(new ImageView("textures/dungeon1-topdoor.png"),
                NodeMatchers.isNotNull());
        FxAssert.verifyThat(new ImageView("textures/dungeon1-bottomdoor.png"),
                NodeMatchers.isNotNull());
        FxAssert.verifyThat(new ImageView("textures/dungeon1-rightdoor.png"),
                NodeMatchers.isNotNull());
        FxAssert.verifyThat(new ImageView("textures/dungeon1-leftdoor.png"),
                NodeMatchers.isNotNull());
    }

    @Test
    public void testDoorsHaveCorrectOrientations() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        GameScreen gs = (GameScreen) Controller.getState();
        assertEquals(gs.getLayout().getStartingRoom().getBottomDoor().getOrientation(),
                DoorOrientation.BOTTOM);
        assertEquals(gs.getLayout().getStartingRoom().getTopDoor().getOrientation(),
                DoorOrientation.TOP);
        assertEquals(gs.getLayout().getStartingRoom().getLeftDoor().getOrientation(),
                DoorOrientation.LEFT);
        assertEquals(gs.getLayout().getStartingRoom().getRightDoor().getOrientation(),
                DoorOrientation.RIGHT);
    }
}
