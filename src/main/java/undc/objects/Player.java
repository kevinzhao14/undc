package undc.objects;

import javafx.scene.image.Image;
import undc.handlers.Audio;
import undc.handlers.Vars;

import java.util.ArrayList;

/**
 * Class that handles the main character of the game controlled by the person playing the game.
 */
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

    /**
     * Creates a Player object.
     * @param maxHealth int maximum value for the player's health stat
     * @param attack double value for player's attack stat
     * @param weapon Weapon that the player stars with
     */
    public Player(int maxHealth, double attack, Weapon weapon) {
        super(maxHealth, attack, Vars.i("sv_player_width"), Vars.i("sv_player_height"), null);
        this.gold = 0;
        this.inventory = new Inventory(Vars.i("sv_inventory_rows"), Vars.i("sv_inventory_cols"));
        inventory.add(weapon);
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

    public void setDirection(int dir) {
        this.setSprite(sprites[dir]);
        direction = dir;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public int getSelected() {
        return this.selected;
    }

    public InventoryItem getItemSelected() {
        return inventory.getItems()[0][selected];
    }

    public void moveRight() {
        this.selected = (this.selected + 1) % inventory.getCols();
    }

    public void moveLeft() {
        this.selected = (this.selected - 1 + inventory.getCols()) % inventory.getCols();
    }

    public void select(int selected) {
        this.selected = selected % inventory.getCols();
    }

    public int getDirection() {
        return direction;
    }

    public ArrayList<Effect> getEffects() {
        return effects;
    }

    /**
     * Sets the entities health to the passed in value unless it is in god mode.
     * @param newHealth New health of the Entity
     */
    public void setHealth(double newHealth) {
        if (newHealth < health && Vars.b("gm_god")) {
            return;
        }
        if (newHealth < health) {
            Audio.playAudio("take_damage");
        }
        super.setHealth(newHealth);
    }
}
