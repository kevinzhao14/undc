package undc.graphics.fxml.controllers;

import undc.command.Vars;
import undc.general.Controller;
import undc.graphics.ConfigScreen;
import undc.graphics.GameScreen;
import undc.graphics.PlayScreen;
import undc.general.Audio;

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
        if (Audio.getAudioClip("menu").isPlaying()) {
            Audio.getAudioClip("menu").stop();
        }
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
        if (Audio.getAudioClip("menu").isPlaying()) {
            Audio.getAudioClip("menu").stop();
        }
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
