package dungeoncrawler.objects;

import dungeoncrawler.handlers.GameSettings;
import javafx.scene.image.Image;

public class Player extends Entity {
    private static Image[] sprites = new Image[]{
        new Image("player/player-left.png"),
        new Image("player/player-up.png"),
        new Image("player/player-right.png"),
        new Image("player/player-down.png")
    };

    private Weapon weapon;
    private int gold;
    private Inventory inventory;

    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, GameSettings.PLAYER_HEIGHT, GameSettings.PLAYER_WIDTH, null);
        this.weapon = weapon;
        this.gold = 0;
        this.inventory = new Inventory(GameSettings.INVENTORY_ROWS, GameSettings.INVENTORY_COLUMNS);
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

}
