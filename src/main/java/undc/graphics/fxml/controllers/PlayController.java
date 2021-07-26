package undc.graphics.fxml.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.general.Controller;
import undc.graphics.GameScreen;
import undc.graphics.HomeScreen;
import undc.graphics.NewGameScreen;
import undc.general.Audio;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class that handles the play screen presented to the player when they select play from the home screen.
 */
public class PlayController {
    @FXML
    private VBox saves;
    @FXML
    private ScrollPane savesScroll;
    @FXML
    private GridPane savesGrid;

    private VBox selected;
    private File selectedFile;
    private JSONObject selectedObj;

    /**
     * Loads all of the saves in the saves folder into the vbox to display.
     */
    public void initialize() {
        savesGrid.setOnScroll(e -> savesScroll.setVvalue(savesScroll.getVvalue() - e.getDeltaY() * 0.0025));

        File dir = new File("saves");
        if (!dir.exists()) {
            return;
        }
        if (!dir.isDirectory()) {
            Console.error("Saves location is not a directory.");
            return;
        }
        File[] files = dir.listFiles();
        if (files == null) {
            Console.error("Failed to retrieve saves.");
            return;
        }
        Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
        for (File f : files) {
            if (f.isDirectory()) {
                continue;
            }
            int i = f.getName().lastIndexOf(".");
            if (i < 0) {
                Console.warn("Invalid save file '" + f.getName() + "'");
                continue;
            }
            String ext = f.getName().substring(i + 1);
            if (!ext.equals("save")) {
                Console.warn("Invalid save file extension '" + ext + "'");
                continue;
            }

            // read file into a string
            String file;
            try {
                file = Files.readString(f.toPath());
            } catch (IOException e) {
                Console.error("Failed to load contents of '" + f.getPath() + "'");
                return;
            }

            // load file data
            JSONObject obj = new JSONObject(file);
            Label name;
            Label date;
            Label mode;
            try {
                name = new Label(obj.getString("name"));
                name.getStyleClass().add("save-name");
            } catch (JSONException e) {
                Console.error("Invalid value for save name.");
                continue;
            }
            try {
                date = new Label("Last played: " + obj.getString("date"));
                date.getStyleClass().add("save-date");
            } catch (JSONException e) {
                Console.error("Invalid value for save date.");
                continue;
            }
            try {
                mode = new Label(obj.getString("mode") + " Mode");
                mode.getStyleClass().add("save-mode");
            } catch (JSONException e) {
                Console.error("Invalid value for save mode.");
                continue;
            }

            // create save item gui
            VBox box = new VBox();
            box.getStyleClass().add("save-item");
            box.getChildren().addAll(name, date, mode);

            box.setOnMouseClicked(e -> {
                if (e.getButton() != MouseButton.PRIMARY) {
                    return;
                }
                if (selected != null) {
                    selected.getStyleClass().remove("save-item-selected");
                }
                if (e.getClickCount() >= 2) {
                    play();
                }
                box.getStyleClass().add("save-item-selected");
                selected = box;
                selectedObj = obj;
                selectedFile = f;
            });

            saves.getChildren().add(box);
        }
    }

    /**
     * Takes the player to the new game screen.
     */
    public void newGame() {
        Audio.playAudio("button");
        NewGameScreen.resetInstance();
        Controller.setState(NewGameScreen.getInstance());
    }

    /**
     * Plays a saved game.
     */
    public void play() {
        Audio.playAudio("button");
        if (selectedObj != null) {
            // load save data & start game
            if (!DataManager.getInstance().loadGame(selectedObj, selectedFile)) {
                return;
            }
            Controller.setState(GameScreen.getInstance());
            GameScreen.getInstance().startLoaded();
            if (Audio.getAudioClip("menu").isPlaying()) {
                Audio.getAudioClip("menu").stop();
            }
        }
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
