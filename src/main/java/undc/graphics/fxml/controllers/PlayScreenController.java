package undc.graphics.fxml.controllers;

import undc.general.Controller;
import undc.graphics.HomeScreen;
import undc.graphics.NewGameScreen;
import undc.general.Audio;

/**
 * Class that handles the play screen presented to the player when they select play from the home screen.
 */
public class PlayScreenController {
    /**
     * Takes the player to the new game screen.
     */
    public void newGame() {
        Audio.playAudio("button");
        NewGameScreen.resetInstance();
        Controller.setState(NewGameScreen.getInstance());
    }

    public void play() {
        Audio.playAudio("button");
    }

    public void edit() {
        Audio.playAudio("button");
    }

    /**
     * Returns the player to the home screen.
     */
    public void back() {
        Audio.playAudio("button");
        HomeScreen.resetInstance();
        Controller.setState(HomeScreen.getInstance());
    }
}
