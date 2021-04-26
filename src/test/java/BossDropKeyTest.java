import dungeoncrawler.controllers.Controller;
import dungeoncrawler.objects.Key;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.MonsterType;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

import java.util.Arrays;

public class BossDropKeyTest extends ApplicationTest {
    private Controller c;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Test
    public void testFinalBossDropItems() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        Monster m = new Monster(1, 1, 1, 1, MonsterType.FINALBOSS, 1, 1);
        // dropped items contains key
        Assert.assertTrue(Arrays.stream(m.dropItems())
                .anyMatch(x -> x.getItem() instanceof Key));
    }


    @Test
    public void testFinalBossSpecialKey() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl wr = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        wr.write("player1");
        clickOn("EASY");
        clickOn("Sword");
        clickOn("Next");

        Monster m = new Monster(1, 1, 1, 1, MonsterType.FINALBOSS, 1, 1);
        // dropped items has Special Key specifically
        Assert.assertTrue(Arrays.stream(m.dropItems())
                .anyMatch(x -> x.getItem().getName().equals("Special Key")));
    }
}
