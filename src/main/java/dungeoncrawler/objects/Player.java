package dungeoncrawler.objects;

import dungeoncrawler.handlers.GameSettings;
import javafx.scene.image.Image;

import java.util.ArrayList;

public class Player extends Entity {
    private static Image[] sprites = new Image[]{
        new Image("player/player-left.png"),
        new Image("player/player-up.png"),
        new Image("player/player-right.png"),
        new Image("player/player-down.png"),
        new Image("player/player-walk-left.gif"),
        new Image("player/player-walk-up.gif"),
        new Image("player/player-walk-right.gif"),
        new Image("player/player-walk-down.gif")
    };

    private int gold;
    private int monstersKilled;
    private double totalDamageDealt;
    private int totalItemsConsumed;

    private Inventory inventory;
    private int selected;
    private int direction;
    private ArrayList<Effect> effects;

    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH, null);
        this.gold = 0;
        this.inventory = new Inventory(GameSettings.INVENTORY_ROWS, GameSettings.INVENTORY_COLUMNS);
        inventory.add(weapon);
        //RangedWeapon w = (RangedWeapon) DataManager.ITEMS[6].copy();
        //Ammo ammo = new Ammo(50, 500, DataManager.PROJECTILES[0].copy());
        //ammo.setRemaining(50);
        //ammo.setBackupRemaining(50);
        //w.setAmmo(ammo);
        //inventory.add(w);
        //inventory.add(DataManager.ITEMS[5], 10);

        monstersKilled = 0;
        totalDamageDealt = 0.0;
        totalItemsConsumed = 0;

        selected = 0;
        direction = 0;
        effects = new ArrayList<>();
    }

    public int getGold() {
        return this.gold;
    }
    public int getMonstersKilled() {
        return monstersKilled;
    }
    public double getTotalDamageDealt() {
        return totalDamageDealt;
    }
    public int getTotalItemsConsumed() {
        return totalItemsConsumed;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }
    public void addMonsterKilled() {
        monstersKilled++;
    }
    public void addDamageDealt(double amt) {
        totalDamageDealt += amt;
    }
    public void addItemConsumed() {
        totalItemsConsumed++;
    }

    public void clearGameStats() {
        //DOES NOT CLEAR GOLD
        monstersKilled = 0;
        totalDamageDealt = 0.0;
        totalItemsConsumed = 0;
    }

    public void setDirection(int dir) {
        this.setImage(sprites[dir]);
        direction = dir;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getSelected() {
        return this.selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public InventoryItem getItemSelected() {
        return inventory.getItems()[0][selected];
    }

    public void moveRight() {
        this.selected = (this.selected + 1) % GameSettings.INVENTORY_COLUMNS;
    }

    public void moveLeft() {
        this.selected = (this.selected - 1 + GameSettings.INVENTORY_COLUMNS)
                % GameSettings.INVENTORY_COLUMNS;
    }

    public void select(int selected) {
        this.selected = selected;
    }

    public int getDirection() {
        return direction;
    }

    public ArrayList<Effect> getEffects() {
        return effects;
    }
}
