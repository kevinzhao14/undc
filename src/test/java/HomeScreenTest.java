import dungeoncrawler.Controller;
import dungeoncrawler.HomeScreen;
import dungeoncrawler.InitPlayerConfigScreen;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.service.query.EmptyNodeQueryException;

import static org.testfx.api.FxAssert.verifyThat;

public class HomeScreenTest extends ApplicationTest {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller controller = new Controller();
        controller.start(primaryStage);
    }

    @Test
    public void containsButtonwithText() {
        FxAssert.verifyThat(".button", LabeledMatchers.hasText("Start"));
    }

    @Test
    public void onButtonClick() {
        clickOn("Start");
        verifyThat("Select difficulty:", NodeMatchers.isNotNull());
        FxAssert.verifyThat(".button", LabeledMatchers.hasText("Next"));
    }
}