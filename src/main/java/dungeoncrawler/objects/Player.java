package dungeoncrawler.objects;

import dungeoncrawler.handlers.GameSettings;
import javafx.scene.image.Image;

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

    private Weapon weapon;
    private int gold;
    private Inventory inventory;
    private int indexOfitemSelected;
    private InventoryItem itemSelected;
//    private Effect status = new Effect[];

    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH, null);
        this.weapon = weapon;
        this.gold = 0;
        this.inventory = new Inventory(GameSettings.INVENTORY_ROWS, GameSettings.INVENTORY_COLUMNS);
        inventory.add(weapon);
        indexOfitemSelected = 0;
        itemSelected = this.inventory.getItems()[0][0];
    }

    public Weapon getWeapon() {
        return this.weapon;
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

    public int getindexOfItemSelected() {
        return this.indexOfitemSelected;
    }

    public void setindexOfItemSelected(int indexOfitemSelected) {
        this.indexOfitemSelected = indexOfitemSelected;
    }

    public InventoryItem getItemSelected() {
        return this.itemSelected;
    }

    public void setItemSelected(InventoryItem itemSelected) {
        this.itemSelected = itemSelected;
    }

    public void moveRight() {
        this.indexOfitemSelected = this.indexOfitemSelected + 1 >= GameSettings.INVENTORY_COLUMNS ? 0 : this.indexOfitemSelected+ 1;
        itemSelected = this.inventory.getItems()[0][indexOfitemSelected];
    }

    public void moveLeft() {
        this.indexOfitemSelected = this.indexOfitemSelected - 1 < 0 ? GameSettings.INVENTORY_COLUMNS - 1 : this.indexOfitemSelected - 1;
        itemSelected = this.inventory.getItems()[0][indexOfitemSelected];
    }

    public void select(int indexOfitemSelected) {
       this.indexOfitemSelected = indexOfitemSelected;
       itemSelected = this.inventory.getItems()[0][indexOfitemSelected];
    }


}
