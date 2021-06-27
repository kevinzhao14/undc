package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.ConfigScreen;
import undc.gamestates.GameScreen;
import undc.gamestates.PlayScreen;
import undc.handlers.Audio;

/**
 * Class that handles the creation of a new game; either story or sandbox mode.
 */
public class NewGameController {

    /**
     * Starts a new story mode game.
     */
    public void newStory() {
        Audio.playAudio("button");
        ConfigScreen.resetInstance();
        Controller.setState(ConfigScreen.getInstance());
    }

    /**
     * Creates a new Sandbox mode.
     */
    public void newSandbox() {
        Audio.playAudio("button");
        GameScreen.resetInstance();
        GameScreen.getInstance().newGame(GameScreen.GameMode.SANDBOX);
        Controller.setState(GameScreen.getInstance());
        GameScreen.getInstance().start();
    }

    /**
     * Returns the player to the play screen.
     */
    public void cancel() {
        Audio.playAudio("button");
        PlayScreen.resetInstance();
        Controller.setState(PlayScreen.getInstance());
    }
}
