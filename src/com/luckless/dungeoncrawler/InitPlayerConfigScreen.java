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

public class InitPlayerConfigScreen extends GameState {
    //Application Window dimensions
    private int windowHeight;
    private int windowWidth;
    private Scene scene;
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
        nextButton.setAlignment(Pos.CENTER);
        this.scene = this.createScene();
    }

    private Scene createScene() {
        Label enterPlayerName = new Label("Enter player name:");
        playerNameEntry = new TextField();
        enterPlayerName.setAlignment(Pos.TOP_LEFT);
        playerNameEntry.setAlignment(Pos.TOP_LEFT);

        Label selectDifficulty = new Label("Select difficulty:");
        selectDifficulty.setAlignment(Pos.TOP_LEFT);
        ToggleGroup difficultyGroup = new ToggleGroup();
        ArrayList<RadioButton> difficultyButtons = new ArrayList<>(
                InitPlayerConfigScreen.Difficulty.values().length);
        for (int i = 0; i < InitPlayerConfigScreen.Difficulty.values().length; i++) {
            difficultyButtons.add(new RadioButton(InitPlayerConfigScreen.Difficulty.values()[i].name()));
            difficultyButtons.get(i).setToggleGroup(difficultyGroup);
        }

        Label selectStarterWeapon = new Label("Select starter weapon:");
        selectStarterWeapon.setAlignment(Pos.TOP_LEFT);
        ToggleGroup weaponGroup = new ToggleGroup();
        ArrayList<RadioButton> weaponButtons = new ArrayList<>(
                InitPlayerConfigScreen.Weapon.values().length);
        for (int i = 0; i < InitPlayerConfigScreen.Weapon.values().length; i++) {
            weaponButtons.add(new RadioButton(InitPlayerConfigScreen.Weapon.values()[i].name()));
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

        Scene scene = new Scene(root, this.windowWidth, this.windowHeight);
        return scene;
    }

    //Implements the getScene() method from abstract class GameState
    public Scene getScene() {return this.scene;}

    public Button getNextButton() {return this.nextButton;}

    public String getEnteredPlayerName() {return this.playerNameEntry.getText();}
}
