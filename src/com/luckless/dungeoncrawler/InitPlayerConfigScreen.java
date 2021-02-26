package view;

import java.util.ArrayList;

import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class InitPlayerConfigScreen {
    //Application Window dimensions
    private int windowHeight;
    private int windowWidth;
    private Button nextButton;
    private TextField playerNameEntry;

    //Stores the entered player name
    private String playerName;

    //Placeholder for difficulty selection, like will be changed later
    private enum Difficulty {EASY, MEDIUM, HARD};
    //Placeholder for weapon type, likely will be changed later
    private enum Weapon {AXE, BROADSWORD, CROSSBOW, MACE};

    public InitPlayerConfigScreen(int width, int height) {
        this.windowWidth = width;
        this.windowHeight = height;
        nextButton = new Button("Next");
    }

    public Scene getScene() {
        Label enterPlayerName = new Label("Enter player name:");
        playerNameEntry = new TextField();

        Label selectDifficulty = new Label("Select difficulty:");
        ToggleGroup difficultyGroup = new ToggleGroup();
        ArrayList<RadioButton> difficultyButtons = new ArrayList<>(
                InitPlayerConfigScreen.Difficulty.values().length);

        for (int i = 0; i < InitPlayerConfigScreen.Difficulty.values().length; i++) {
            difficultyButtons.add(new RadioButton(InitPlayerConfigScreen.Difficulty.values()[i].name()));
            difficultyButtons.get(i).setToggleGroup(difficultyGroup);
        }

        Label selectStarterWeapon = new Label("Select starter weapon:");
        ToggleGroup weaponGroup = new ToggleGroup();
        ArrayList<RadioButton> weaponButtons = new ArrayList<>(
                InitPlayerConfigScreen.Weapon.values().length);
        for (int i = 0; i < InitPlayerConfigScreen.Weapon.values().length; i++) {
            weaponButtons.add(new RadioButton(InitPlayerConfigScreen.Weapon.values()[i].name()));
            weaponButtons.get(i).setToggleGroup(weaponGroup);
        }

        GridPane root = new GridPane();
        root.setVgap(5);
        root.setHgap(5);

        //Add player name row
        root.add(enterPlayerName, 0, 0);
        root.add(playerNameEntry, 1, 0);

        //Add difficulty row
        root.add(selectDifficulty, 0, 1);
        selectDifficulty.setAlignment(Pos.TOP_CENTER);
        VBox difficultyVBox = new VBox(5);
        for (RadioButton btn : difficultyButtons) {
            difficultyVBox.getChildren().add(btn);
        }
        root.add(difficultyVBox, 1, 1);

        //Add starting weapon row
        root.add(selectStarterWeapon, 0, 2);
        VBox weaponVBox = new VBox(5);
        for (RadioButton btn : weaponButtons) {
            weaponVBox.getChildren().add(btn);
        }
        root.add(weaponVBox, 1, 2);

        root.add(nextButton, 1, 3);

        Scene scene = new Scene(root, this.windowWidth, this.windowHeight);
        return scene;
    }

    public Button getNextButton() { return this.nextButton; }

    public String getEnteredPlayerName() {
        return this.playerNameEntry.getText();
    }
}
