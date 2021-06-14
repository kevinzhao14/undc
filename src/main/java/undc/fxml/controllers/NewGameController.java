package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.ConfigScreen;
import undc.gamestates.GameScreen;
import undc.gamestates.PlayScreen;

public class NewGameController {

    public void newStory() {
        ConfigScreen.resetInstance();
        Controller.setState(ConfigScreen.getInstance());
    }

    public void newSandbox() {
        GameScreen.resetInstance();
        GameScreen.getInstance().newGame(GameScreen.GameMode.SANDBOX);
        Controller.setState(GameScreen.getInstance());
        GameScreen.getInstance().start();
    }

    public void cancel() {
        PlayScreen.resetInstance();
        Controller.setState(PlayScreen.getInstance());
    }
}
