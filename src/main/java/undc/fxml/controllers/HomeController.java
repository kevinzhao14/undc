package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.PlayScreen;
import undc.gamestates.SettingsScreen;

/**
 * Class that handles the functionality of HomeScreen.fxml.
 */
public class HomeController {

    public void play() {
        PlayScreen.resetInstance();
        Controller.setState(PlayScreen.getInstance());
    }

    public void settings() {
        SettingsScreen.resetInstance();
        Controller.setState(SettingsScreen.getInstance());
    }

    public void quit() {
        Controller.quit();
    }
}
