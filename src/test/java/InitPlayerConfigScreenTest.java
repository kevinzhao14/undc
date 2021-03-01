import dungeoncrawler.Controller;
import dungeoncrawler.HomeScreen;
import dungeoncrawler.InitPlayerConfigScreen;

import javafx.stage.Stage;
import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.robot.impl.*;
import org.testfx.service.finder.impl.WindowFinderImpl;
import org.testfx.service.query.EmptyNodeQueryException;

import static org.testfx.api.FxAssert.verifyThat;

public class InitPlayerConfigScreenTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Controller testController = new Controller();
        testController.start(stage);
    }

    /*
    @Test
    public void testTextField() {

    }
    */

    @Test
    public void testNextButton() {
        /*
        Check if next button successfully sends player to first room if
        valid player name is entered, and a difficulty and weapon
        RadioButton are both selected
        */
        clickOn("Start");
        verifyThat("Enter player name:", NodeMatchers.isNotNull());

        //Enter player name as "player1"
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        //select difficulty
        clickOn("EASY");
        //select weapon
        clickOn("Sword");
        //click next button
        clickOn("Next");
        //confirm player has reached FirstRoom screen
        try {
            verifyThat("ROOM 1", NodeMatchers.isNotNull());
        } catch (EmptyNodeQueryException e) {
            e.printStackTrace();
            //indicate test has failed
            Assert.assertTrue(false);
        }

        //indicate test has passed
        Assert.assertTrue(true);
    }

}
