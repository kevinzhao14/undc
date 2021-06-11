package undc.gamestates;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

import undc.objects.Player;

public class Hud {
    private final StackPane hud;

    public Hud(Player player) {
        hud = new StackPane();

        GridPane grid = new GridPane();
        // gridpane columns
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(70);
        ColumnConstraints col3 = new ColumnConstraints();
        grid.getColumnConstraints().addAll(col1, col2, col3);

        // gridpane rows
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.SOMETIMES);
        grid.getRowConstraints().addAll(row1, row2);

        HBox playerInfo = new HBox();
        playerInfo.setId("player-info");

        // player image/profile
        HBox playerImageBox = new HBox();
        ImageView playerImage = new ImageView("player/profile.png");
        playerImageBox.getChildren().add(playerImage);

        // player stats/info
        VBox playerStats = new VBox();

        // player gold
        HBox playerGoldBox = new HBox();
        Label playerGold = new Label("Gold: 0");
        playerGoldBox.getChildren().add(playerGold);

        // player healthbar
        StackPane playerHealthBarPane = new StackPane();
        Rectangle healthBarOuter = new Rectangle(200, 40);
        Rectangle healthBarInner = new Rectangle(200, 40);
        Label healthBarText = new Label("100");
        playerHealthBarPane.getChildren().addAll(healthBarOuter, healthBarInner, healthBarText);

        playerStats.getChildren().addAll(playerGoldBox, playerHealthBarPane);
        playerInfo.getChildren().addAll(playerImageBox, playerStats);
        GridPane.setConstraints(playerInfo, 0, 1);

        // hotbar
        HBox hotbarBox = new HBox();
        for (int i = 0; i < player.getInventory().getCols(); i++) {
            VBox temp = new VBox();
            temp.setPrefHeight(100);
            temp.setPrefWidth(100);

            hotbarBox.getChildren().add(temp);
        }
        GridPane.setConstraints(hotbarBox, 1, 1);

        // ammo counter
        HBox ammoBox = new HBox();
        Label ammoCounter = new Label("Ammo: 0 / 0");
        ammoBox.getChildren().add(ammoCounter);
        GridPane.setConstraints(ammoBox, 2, 1);

        // effects display
        VBox effectsBox = new VBox();
        for (int i = 0; i < 3; i++) {
            ImageView temp = new ImageView("effects/attackboost.png");
            effectsBox.getChildren().add(temp);
        }
        GridPane.setConstraints(effectsBox, 2, 0);

        grid.getChildren().addAll(playerInfo, hotbarBox, ammoBox, effectsBox);
        hud.getChildren().add(grid);
        hud.getStylesheets().add("styles/hud.css");


        hud.setStyle("-fx-border-color: blue; -fx-border-width: 2px");
        grid.setStyle("-fx-border-color: red; -fx-border-width: 2px");
        System.out.println(grid.getRowCount());
        System.out.println(grid.getColumnCount());
    }

    public StackPane getHud() {
        return hud;
    }
}
