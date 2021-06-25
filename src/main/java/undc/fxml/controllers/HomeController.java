package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.PlayScreen;
import undc.gamestates.SettingsScreen;
import undc.handlers.Audio;

/**
 * Class that handles the functionality of HomeScreen.fxml.
 */
public class HomeController {

    public void play() {
        Audio.playAudio("button");
        PlayScreen.resetInstance();
        Controller.setState(PlayScreen.getInstance());
    }

    public void settings() {
        Audio.playAudio("button");
        SettingsScreen.resetInstance();
        Controller.setState(SettingsScreen.getInstance());
    }

    public void quit() {
        Controller.quit();
    }
}
