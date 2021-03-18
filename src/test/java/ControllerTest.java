import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.HomeScreen;
import dungeoncrawler.gamestates.InitPlayerConfigScreen;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;

import org.testfx.framework.junit.ApplicationTest;

public class ControllerTest extends ApplicationTest {
    @Override
    public void start(Stage stage) {
        Controller c = new Controller();
        c.start(stage);
    }

    @Test
    public void controllerBeginsOnHomescreen() {
        Assert.assertTrue(Controller.getState() instanceof HomeScreen);
    }

    @Test
    public void setStateChangesGameState() {
        Assert.assertFalse(Controller.getState() instanceof InitPlayerConfigScreen);
        interact(() -> Controller.setState(new InitPlayerConfigScreen(10, 10)));
        Assert.assertTrue(Controller.getState() instanceof InitPlayerConfigScreen);
    }
}
