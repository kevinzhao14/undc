package undc.graphics;

import java.util.ArrayList;

import undc.general.Audio;
import undc.game.Difficulty;
import undc.command.Vars;
import undc.items.Weapon;
import undc.general.Controller;
import undc.command.DataManager;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Class implementation of the initial player configuration screen.
 */
public class ConfigScreen extends GameState {
    private static ConfigScreen instance;

    private final TextField playerNameEntry;

    /**
     * Constructor for creating instance of InitPlayerConfigScreen.
     *
     * @param width width of scene window, in pixels
     * @param height height of the scene window, in pixels
     */
    public ConfigScreen(int width, int height) {
        super(width, height);
        //Application Window dimensions
        Button nextButton = new Button("Next");
        nextButton.setAlignment(Pos.CENTER);

        Label enterPlayerName = new Label("Enter Game Name:");
        playerNameEntry = new TextField();
        enterPlayerName.setAlignment(Pos.TOP_LEFT);
        playerNameEntry.setAlignment(Pos.TOP_LEFT);

        Label selectDifficulty = new Label("Select difficulty:");
        selectDifficulty.setAlignment(Pos.TOP_LEFT);
        ToggleGroup difficultyGroup = new ToggleGroup();
        ArrayList<RadioButton> difficultyButtons = new ArrayList<>(
                Difficulty.values().length);
        for (int i = 0; i < Difficulty.values().length; i++) {
            difficultyButtons.add(new RadioButton(Difficulty.values()[i].name()));
            difficultyButtons.get(i).setToggleGroup(difficultyGroup);
        }

        Label selectStarterWeapon = new Label("Select starter weapon:");
        selectStarterWeapon.setAlignment(Pos.TOP_LEFT);
        ToggleGroup weaponGroup = new ToggleGroup();
        ArrayList<RadioButton> weaponButtons = new ArrayList<>(DataManager.getStartingWeapons().length);
        for (int i = 0; i < DataManager.getStartingWeapons().length; i++) {
            Weapon w = DataManager.getStartingWeapons()[i];
            RadioButton button = new RadioButton(w.getName());
            button.setId(w.getId());
            weaponButtons.add(button);
            weaponButtons.get(i).setToggleGroup(weaponGroup);
        }

        GridPane root = new GridPane();
        root.setVgap(20.0);
        root.setHgap(20.0);
        root.setAlignment(Pos.CENTER);

        //Add player name row
        VBox row1 = new VBox(5);
        row1.getChildren().addAll(enterPlayerName, playerNameEntry);
        root.add(row1, 0, 0);
        GridPane.setColumnSpan(row1, 2);

        //Create difficulty row
        GridPane row2 = new GridPane();
        row2.setVgap(10.0);
        row2.setHgap(10.0);
        row2.add(selectDifficulty, 0, 0);
        for (int i = 0; i < difficultyButtons.size(); i++) {
            row2.add(difficultyButtons.get(i), 1, i);
        }
        row2.add(selectStarterWeapon, 0, difficultyButtons.size() + 1);
        for (int i = 0; i < weaponButtons.size(); i++) {
            row2.add(weaponButtons.get(i), 1, i + difficultyButtons.size() + 1);
        }

        //Add row2 to root
        root.add(row2, 0, 1);
        GridPane.setColumnSpan(row2, 2);

        VBox row3 = new VBox(5);
        //Adjust button size
        nextButton.setMaxWidth(100.0);

        row3.getChildren().add(nextButton);
        row3.setAlignment(Pos.CENTER_RIGHT);

        //Add row3 to root
        root.add(row3, 1, 2);

        nextButton.setOnAction(e -> {
            Vars.set("cheats", "false");
            Audio.playAudio("button");
            String playerName = playerNameEntry.getText();
            RadioButton selectedDifficulty = (RadioButton) difficultyGroup.getSelectedToggle();
            RadioButton selectedWeapon = (RadioButton) weaponGroup.getSelectedToggle();
            String weaponId = (selectedWeapon == null) ? null : selectedWeapon.getId();

            Weapon weaponRef = null;
            Difficulty difficultyRef;

            for (Weapon w : DataManager.getStartingWeapons()) {
                if (w.getId().equals(weaponId)) {
                    weaponRef = w;
                    break;
                }
            }

            difficultyRef = (selectedDifficulty == null)
                    ? null : Difficulty.valueOf(selectedDifficulty.getText());

            try {
                DataManager.getInstance().newGame(playerName, difficultyRef, weaponRef);
                GameScreen.resetInstance();
                GameScreen gameScreen = GameScreen.getInstance();
                gameScreen.newGame(GameScreen.GameMode.STORY);
                Controller.setState(gameScreen);
                gameScreen.start();
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                Alert alert = new Alert(AlertType.ERROR, iae.getMessage());
                alert.showAndWait();
            }
        });


        this.scene.setRoot(root);
        scene.getStylesheets().addAll("styles/config.css", "styles/global.css");
        enterPlayerName.getStyleClass().add("label");
        selectDifficulty.getStyleClass().add("label");
        selectStarterWeapon.getStyleClass().add("label");
        for (RadioButton r : difficultyButtons) {
            r.getStyleClass().add("radioButton");
        }
        for (RadioButton r : weaponButtons) {
            r.getStyleClass().add("radioButton");
        }
        nextButton.getStyleClass().add("nextButton");
    }

    /**
     * Method returning the current instance of the game, acts as a singleton.
     * @return ConfigScreen that is the current game state.
     */
    public static ConfigScreen getInstance() {
        if (instance == null) {
            resetInstance();
        }
        return instance;
    }

    public static void resetInstance() {
        instance = new ConfigScreen(Vars.i("gc_screen_width"), Vars.i("gc_screen_height"));
    }
}
