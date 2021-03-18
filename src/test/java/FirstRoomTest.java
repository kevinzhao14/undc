import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.gamestates.FirstRoom;
import dungeoncrawler.handlers.Difficulty;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;

import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

public class FirstRoomTest extends ApplicationTest {

    private Controller c;
    private DataManager d;
    private Stage s;

    @Override
    public void start(Stage stage) {
        c = new Controller();
        c.start(stage);
    }


    @Test
    public void goldDisplaysOnScreen() {
        c = new Controller();
        c.getDataManager().newGame("name", Difficulty.EASY, DataManager.WEAPONS[0]);
        interact(() -> Controller.setState(new FirstRoom(10, 10)));
        FxAssert.verifyThat(".label", LabeledMatchers.hasText("Gold: 300"));
    }


    @Test
    public void difficultyAffectsStartingGold() {
        c = new Controller();
        c.getDataManager().newGame("name", Difficulty.EASY, DataManager.WEAPONS[0]);
        FirstRoom firstRoom = new FirstRoom(10, 10);
        FirstRoom finalFirstRoom = firstRoom;
        interact(() -> Controller.setState(finalFirstRoom));
        Assert.assertEquals(300, firstRoom.getGold());

        Controller.getDataManager().newGame("name", Difficulty.MEDIUM,
                c.getDataManager().WEAPONS[0]);
        firstRoom = new FirstRoom(10, 10);
        Assert.assertEquals(200, firstRoom.getGold());

        c.getDataManager().newGame("name", Difficulty.HARD, c.getDataManager().WEAPONS[0]);
        firstRoom = new FirstRoom(10, 10);
        Assert.assertEquals(100, firstRoom.getGold());
    }


}
