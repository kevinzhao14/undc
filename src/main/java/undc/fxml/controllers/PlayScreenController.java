package undc.fxml.controllers;

import undc.controllers.Controller;
import undc.gamestates.GameScreen;
import undc.gamestates.HomeScreen;
import undc.handlers.Vars;

public class PlayScreenController {
    public void newGame() {
        System.out.println("New game");
        GameScreen.getInstance().newGame(GameScreen.GameMode.SANDBOX);
        Controller.setState(GameScreen.getInstance());
        GameScreen.getInstance().start();
    }

    public void play() {
        System.out.println("Play save");
    }

    public void edit() {
        System.out.println("Edit save");
    }

    public void back() {
        Controller.setState(new HomeScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height")));
    }
}
