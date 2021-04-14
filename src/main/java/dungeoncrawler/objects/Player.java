package dungeoncrawler.objects;

import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.handlers.GameSettings;
import javafx.scene.image.Image;

import javax.xml.crypto.Data;

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
    private Inventory inventory;
    private int selected;
    //private Effect status = new Effect[];

    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH, null);
        this.gold = 0;
        this.inventory = new Inventory(GameSettings.INVENTORY_ROWS, GameSettings.INVENTORY_COLUMNS);
        inventory.add(weapon);
        inventory.add(DataManager.ITEMS[0].copy(), 5);
        selected = 0;
    }

    public int getGold() {
        return this.gold;
    }
    public void setGold(int gold) {
        this.gold = gold;
    }

    public void setDirection(int dir) {
        this.setImage(sprites[dir]);
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


}
