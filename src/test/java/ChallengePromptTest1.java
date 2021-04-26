import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.DungeonLayout;
import dungeoncrawler.objects.Room;
import dungeoncrawler.objects.RoomType;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;


public class ChallengePromptTest1 extends ApplicationTest {

    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }


    @Test
    public void testPopupVisible() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        DungeonLayout dl = ((GameScreen) c.getState()).getLayout();

        int i = 0;
        int j = 0;
        Room challenge = dl.getGrid()[i][j];
        while (challenge == null || !(challenge.getType() == RoomType.CHALLENGEROOM)) {
            if (j < 14) {
                j++;
            } else {
                i++;
                j = 0;
            }
            challenge = dl.getGrid()[i][j];
        }

        ((GameScreen) c.getState()).getGame().setRoom(challenge);

        FxAssert.verifyThat(new Label("You have entered a Challenge Room."
                        + "Would you like to partake in the trial?"),
                NodeMatchers.isNotNull());

    }

}
