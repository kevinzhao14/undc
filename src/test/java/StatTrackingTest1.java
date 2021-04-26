import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.Player;
import javafx.stage.Stage;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;


public class StatTrackingTest1 extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testStatsCleared() {
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

        p.clearGameStats();
        assertEquals(0.0, p.getTotalDamageDealt(), 0.0);
        assertEquals(0.0, p.getTotalItemsConsumed(), 0.0);
        assertEquals(0.0, p.getMonstersKilled(), 0.0);
    }
}
