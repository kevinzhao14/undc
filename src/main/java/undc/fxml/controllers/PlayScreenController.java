package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.GameScreen;
import undc.gamestates.HomeScreen;

public class PlayScreenController {
    public void newGame() {
        GameScreen.getInstance().newGame(GameScreen.GameMode.SANDBOX);
        Controller.setState(GameScreen.getInstance());
        GameScreen.getInstance().start();
    }

    public void play() {
    }

    public void edit() {
    }

    public void back() {
        Controller.setState(HomeScreen.getInstance());
    }
}
