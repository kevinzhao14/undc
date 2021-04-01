import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.Player;
import dungeoncrawler.objects.Room;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.robot.impl.BaseRobotImpl;
import org.testfx.robot.impl.SleepRobotImpl;
import org.testfx.robot.impl.WriteRobotImpl;
import org.testfx.service.finder.impl.WindowFinderImpl;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class RestartGameTest extends ApplicationTest {
    private Controller c;
    private Player p;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }

    @Before
    public void setUp() {
        clickOn("Start");
        clickOn("Enter player name:");
        WriteRobotImpl robot = new WriteRobotImpl(
                new BaseRobotImpl(), new SleepRobotImpl(), new WindowFinderImpl());
        robot.write("test");

        //randomly select difficulty to ensure that player has default gold after restart
        String[] difficulties = {"EASY", "MEDIUM", "HARD"};
        Random r = new Random();
        clickOn(difficulties[r.nextInt(difficulties.length)]);

        clickOn("Sword");
        clickOn("Next");

        p = ((GameScreen) c.getState()).getPlayer();

        //Initiate game over screen
        Platform.runLater(() -> {
            ((GameScreen) c.getState()).gameOver();
            ((GameScreen) c.getState()).restartGame();
        });
    }

    @Test
    public void testRoomsAreRestored() {
        Room startingRoom = ((GameScreen) c.getState()).getLayout().getStartingRoom();
        Room[][] mapGrid = ((GameScreen) c.getState()).getLayout().getGrid();

        for (Room[] roomRow : mapGrid) {
            for (Room r : roomRow) {
                if (r != null && r != startingRoom) {
                    assertEquals(r.wasVisited(), false);
                    for (Monster m : r.getMonsters()) {
                        if (m != null) {
                            assertEquals(m.getHealth(), m.getMaxHealth(), 0);
                        }
                    }
                }
            }
        }
    }


}
