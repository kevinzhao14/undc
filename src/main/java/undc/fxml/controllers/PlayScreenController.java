package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.HomeScreen;
import undc.gamestates.NewGameScreen;

/**
 * Class that handles the play screen presented to the player when they select play from the home screen.
 */
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
