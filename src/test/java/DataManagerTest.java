import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.handlers.Difficulty;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DataManagerTest {

    private DataManager dataManager;

    @Before
    public void setup() {
        dataManager = new DataManager();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewGame() {
        dataManager.newGame("     ", Difficulty.EASY, dataManager.WEAPONS[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewGame2() {
        dataManager.newGame("username", null, dataManager.WEAPONS[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewGame3() {
        dataManager.newGame("username", Difficulty.EASY, null);
    }

    @Test
    public void testNewGame4() {
        boolean result = dataManager.newGame("username", Difficulty.EASY, dataManager.WEAPONS[0]);
        assertEquals(true, result);
    }
}
