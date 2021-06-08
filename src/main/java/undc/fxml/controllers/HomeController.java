package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.PlayScreen;
import undc.gamestates.SettingsScreen;

public class HomeController {

    public void play() {
        Controller.setState(PlayScreen.getInstance());
    }

    public void settings() {
        Controller.setState(SettingsScreen.getInstance());
    }

    public void quit() {
        Controller.quit();
    }
}
