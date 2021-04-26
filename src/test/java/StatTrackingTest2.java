import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.Player;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.Test;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;


public class StatTrackingTest2 extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testStatsDisplayed() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl r = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        r.write("name");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        Player p = ((GameScreen) c.getState()).getPlayer();

        //change player stats manually
        double dmgAmt = 400.0;
        int numItems = 2;
        int numMonsters = 3;
        p.addDamageDealt(dmgAmt);
        for (int i = 0; i < numItems; i++) {
            p.addItemConsumed();
        }
        for (int i = 0; i < numMonsters; i++) {
            p.addMonsterKilled();
        }

        Platform.runLater(() -> {
            ((GameScreen) c.getState()).gameOver();
            verifyThat(new Label("" + dmgAmt), NodeMatchers.isNotNull());
            verifyThat(new Label("" + numItems), NodeMatchers.isNotNull());
            verifyThat(new Label("" + numMonsters), NodeMatchers.isNotNull());
        });

    }
}