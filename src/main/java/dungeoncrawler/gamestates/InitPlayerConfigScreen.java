package dungeoncrawler.gamestates;

import java.util.ArrayList;

import dungeoncrawler.handlers.Difficulty;
import dungeoncrawler.objects.Weapon;
import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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
 * Class implementation of the initial player configuration screen
 * for the Team Luckless Dungeon Crawler game.
 *
 * @author Manas Harbola
 * @version 1.0
 * @userid mharbola3
 */
public class InitPlayerConfigScreen extends GameState {
    //Application Window dimensions
    private int windowHeight;
    private int windowWidth;
    private Button nextButton;
    private TextField playerNameEntry;

    //Stores the entered player name
    private String playerName;

    /**
     * Constructor for creating instance of InitPlayerConfigScreen.
     *
     * Constructor generates a JavaFX Scene object of specified window size
     * for the initial configuration screen, where the player selects their name,
     * game difficulty, and starter weapon. This Scene is then set by the Controller
     * object onto the Stage.
     *
     * Player specified name, difficulty, weapon, are all checked for validity
     * when they are sent to the DataManager object for storing player configuration.
     * If any of the three fields are invalid, a JavaFX ERROR Alert window is displayed
     * to the player, prompting them on which field is invalid and must be fixed before
     * proceeding into the game.
     *
     * @param width width of scene window, in pixels
     * @param height height of the scene window, in pixels
     */
    public InitPlayerConfigScreen(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        nextButton = new Button("Next");
        nextButton.setAlignment(Pos.CENTER);


        Label enterPlayerName = new Label("Enter player name:");
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
        ArrayList<RadioButton> weaponButtons = new ArrayList<>(
                DataManager.WEAPONS.length);
        for (int i = 0; i < DataManager.WEAPONS.length; i++) {
            weaponButtons.add(new RadioButton(DataManager.WEAPONS[i].getName()));
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
        root.setColumnSpan(row1, 2);

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
        root.setColumnSpan(row2, 2);

        VBox row3 = new VBox(5);
        //Adjust button size
        nextButton.setMaxWidth(100.0);

        row3.getChildren().add(nextButton);
        row3.setAlignment(Pos.CENTER_RIGHT);

        //Add row3 to root
        root.add(row3, 1, 2);

        nextButton.setOnAction(e -> {
            String playerName = playerNameEntry.getText();
            RadioButton selectedDifficulty = (RadioButton) difficultyGroup.getSelectedToggle();
            RadioButton selectedWeapon = (RadioButton) weaponGroup.getSelectedToggle();
            String weaponName = (selectedWeapon == null) ? null : selectedWeapon.getText();

            Weapon weaponRef = null;
            Difficulty difficultyRef;

            for (Weapon w : DataManager.WEAPONS) {
                if (w.getName().equals(weaponName)) {
                    weaponRef = w;
                    break;
                }
            }

            difficultyRef = (selectedDifficulty == null)
                    ? null : Difficulty.valueOf(selectedDifficulty.getText());

            try {
                if (Controller.getDataManager().newGame(playerName, difficultyRef, weaponRef)) {
                    GameScreen gameScreen = new GameScreen(width, height);
                    Controller.setState(gameScreen);
                    gameScreen.start();
                }
            } catch (IllegalArgumentException iae) {
                Alert alert = new Alert(AlertType.ERROR, iae.getMessage());
                alert.showAndWait();
            }
        });


        this.scene = new Scene(root, this.windowWidth, this.windowHeight);
        scene.getStylesheets().addAll("styles/InitPlayerConfigScreen.css",
                "http://fonts.googleapis.com/css?family=VT323");
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
}
