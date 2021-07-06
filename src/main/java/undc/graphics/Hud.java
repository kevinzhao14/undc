package undc.graphics;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
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

import javafx.util.Duration;
import undc.command.Console;
import undc.general.Controller;
import undc.game.Effect;
import undc.game.EffectType;
import undc.inventory.InventoryItem;
import undc.item.Item;
import undc.entity.Player;
import undc.item.RangedWeapon;
import undc.item.WeaponAmmo;

/**
 * Class that handles the graphics and functionality of the hud.
 */
public class Hud {
    private static final int HEALTHBAR_HEIGHT = 30;
    private static final int HEALTHBAR_WIDTH = 200;
    private static final int XPBAR_HEIGHT = 20;
    private static final int XPBAR_WIDTH = 200;

    private final StackPane hud;
    private final Label playerGold;
    private final Rectangle healthBarInner;
    private final Label healthBarText;
    private final Rectangle xpBarInner;
    private final Label xpBarText;
    private final HBox hotbar;
    private final Label ammoCounter;
    private final VBox effectsBox;
    private final HBox playerImageOverlay;

    private FadeTransition animation;

    /**
     * Creates a hude for the player.
     * @param player Player to display hud for
     */
    public Hud(Player player) {
        hud = new StackPane();
        hud.setId("hud");

        GridPane grid = new GridPane();
        grid.setId("grid");

        // gridpane columns
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        col1.setMinWidth(10);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        col3.setMinWidth(10);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        // gridpane rows
        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.SOMETIMES);
        grid.getRowConstraints().addAll(row1, row2);

        HBox playerInfo = new HBox();
        playerInfo.setId("player-info");

        // player image/profile
        StackPane playerImageBox = new StackPane();
        playerImageBox.setId("player-image");
        ImageView playerImage = new ImageView("player/profile.png");
        playerImage.setFitHeight(150);
        playerImage.setFitWidth(150);
        playerImageOverlay = new HBox();
        playerImageOverlay.setPrefHeight(150);
        playerImageOverlay.setPrefWidth(150);
        playerImageOverlay.setId("image-overlay");
        playerImageBox.getChildren().addAll(playerImage, playerImageOverlay);

        // player stats/info
        VBox playerStats = new VBox();
        playerStats.setId("player-stats");

        // player gold
        HBox playerGoldBox = new HBox();
        playerGoldBox.setId("player-gold");
        playerGold = new Label("Gold: 0");
        playerGoldBox.getChildren().add(playerGold);

        // player healthbar
        StackPane healthBarPane = new StackPane();
        healthBarPane.setId("healthbar");
        Rectangle healthBarOuter = new Rectangle(HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT);
        healthBarOuter.setId("healthbar-outer");
        healthBarInner = new Rectangle(HEALTHBAR_WIDTH, HEALTHBAR_HEIGHT);
        healthBarInner.setId("healthbar-inner");
        healthBarText = new Label("100 HP");
        healthBarPane.getChildren().addAll(healthBarOuter, healthBarInner, healthBarText);

        // player xp bar
        StackPane xpBarPane = new StackPane();
        xpBarPane.setId("xpbar");
        Rectangle xpBarOuter = new Rectangle(XPBAR_WIDTH, XPBAR_HEIGHT);
        xpBarOuter.setId("xpbar-outer");
        xpBarInner = new Rectangle(XPBAR_WIDTH / 2.0, XPBAR_HEIGHT);
        xpBarInner.setId("xpbar-inner");
        xpBarText = new Label("Level 0");
        xpBarPane.getChildren().addAll(xpBarOuter, xpBarInner, xpBarText);

        playerStats.getChildren().addAll(playerGoldBox, healthBarPane, xpBarPane);
        playerInfo.getChildren().addAll(playerImageBox, playerStats);
        GridPane.setConstraints(playerInfo, 0, 1);

        // hotbar
        hotbar = new HBox();
        hotbar.setId("hotbar");
        for (int i = 0; i < player.getInventory().getCols(); i++) {
            VBox temp = new VBox();
            hotbar.getChildren().add(temp);
        }
        GridPane.setConstraints(hotbar, 1, 1);

        // ammo counter
        HBox ammoBox = new HBox();
        ammoBox.setId("ammo");
        ammoCounter = new Label("Ammo: 0 / 0");
        ammoCounter.setVisible(false);
        ammoBox.getChildren().add(ammoCounter);
        GridPane.setConstraints(ammoBox, 2, 1);

        // effects display
        effectsBox = new VBox();
        effectsBox.setId("effects");
        GridPane.setConstraints(effectsBox, 2, 0);

        grid.getChildren().addAll(playerInfo, hotbar, ammoBox, effectsBox);
        hud.getChildren().add(grid);
        hud.getStylesheets().add("styles/hud.css");
    }

    /**
     * Loads in new information for the hud and changes data and graphics accordingly.
     */
    public void update() {
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Invalid state. " + Controller.getState());
            return;
        }
        Player player = GameScreen.getInstance().getPlayer();

        // update gold
        playerGold.setText("Gold: " + player.getGold());

        // update health
        healthBarInner.setWidth(HEALTHBAR_WIDTH * (player.getHealth() / player.getMaxHealth()));
        healthBarText.setText((int) Math.ceil(player.getHealth()) + " HP");

        // update xp
        xpBarInner.setWidth(XPBAR_WIDTH * ((double) player.getXp() / Player.xpNeeded(player.getLevel())));
        xpBarText.setText("Level " + player.getLevel());

        // update hotbar
        InventoryItem[] inv = player.getInventory().getItems()[0];
        for (int i = 0; i < inv.length; i++) {
            Node node = hotbar.getChildren().get(i);
            if (!(node instanceof VBox)) {
                Console.error("Failed to update hotbar.");
                return;
            }
            VBox box = (VBox) node;
            if (player.getSelected() == i) {
                box.getStyleClass().add("hotbar-selected");
            } else if (box.getStyleClass().contains("hotbar-selected")) {
                box.getStyleClass().clear();
            }
            if (inv[i] == null) {
                box.getChildren().clear();
                continue;
            }
            Item item = inv[i].getItem();
            ImageView image = new ImageView(item.getSprite());
            image.setFitHeight(50);
            image.setFitWidth(50);
            if (box.getChildren().size() == 0 || !box.getChildren().get(0).equals(image)) {
                box.getChildren().clear();
                box.getChildren().add(image);
            }
        }

        // update ammo
        InventoryItem selected = player.getItemSelected();
        if (selected != null && selected.getItem() instanceof RangedWeapon) {
            WeaponAmmo ammo = ((RangedWeapon) selected.getItem()).getAmmo();
            ammoCounter.setVisible(true);
            ammoCounter.setText(ammo.getRemaining() + " / " + ammo.getBackupRemaining());
        } else if (ammoCounter.isVisible()) {
            ammoCounter.setVisible(false);
        }

        // update effects
        effectsBox.getChildren().clear();
        for (Effect e : player.getEffects()) {
            ImageView temp;
            if (e.getType() == EffectType.ATTACKBOOST) {
                temp = new ImageView("effects/attackboost.png");
            } else {
                Console.error("Failed to update effects.");
                return;
            }
            temp.setFitWidth(40);
            temp.setFitHeight(40);
            effectsBox.getChildren().add(temp);
        }
    }

    public StackPane getHud() {
        return hud;
    }

    /**
     * Animates the overlay to show and hide (currently used as damage indicator).
     */
    public void showOverlay() {
        if (animation != null) {
            animation.stop();
        }
        animation = new FadeTransition(Duration.millis(150), playerImageOverlay);
        animation.setFromValue(0);
        animation.setToValue(1);
        animation.setOnFinished(e -> {
            animation = new FadeTransition(Duration.millis(300), playerImageOverlay);
            animation.setFromValue(1);
            animation.setToValue(0);
            animation.setOnFinished(e2 -> animation = null);
            animation.play();
        });
        animation.play();
    }
}
