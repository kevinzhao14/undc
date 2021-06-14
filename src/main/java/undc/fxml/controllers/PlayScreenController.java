package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.GameScreen;
import undc.gamestates.HomeScreen;
import undc.gamestates.NewGameScreen;

public class PlayScreenController {
    public void newGame() {
        NewGameScreen.resetInstance();
        Controller.setState(NewGameScreen.getInstance());
    }

    public void play() {
    }

    public void edit() {
    }

    public void back() {
        HomeScreen.resetInstance();
        Controller.setState(HomeScreen.getInstance());
    }
}
