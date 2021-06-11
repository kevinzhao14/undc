package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.ConfigScreen;
import undc.gamestates.GameScreen;

public class NewGameController {

    public void newStory() {
        Controller.setState(ConfigScreen.getInstance());
    }

    public void newSandbox() {
        GameScreen.getInstance().newGame(GameScreen.GameMode.SANDBOX);
        Controller.setState(GameScreen.getInstance());
        GameScreen.getInstance().start();
    }
}
