package undc.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import undc.gamestates.GameScreen;
import undc.handlers.Audio;
import undc.handlers.Vars;

import java.util.ArrayList;

/**
 * Class that handles the main character of the game controlled by the person playing the game.
 */
public class Player extends Entity implements Savable {
    private static final Image[] SPRITES = new Image[]{
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
    private final ArrayList<Effect> effects;

    private Inventory inventory;
    private int selected;
    private int direction;
    private int level;
    private int xp;

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
        this.setSprite(SPRITES[dir]);
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
            GameScreen.getInstance().getHud().showOverlay();
        }
        super.setHealth(newHealth);
    }

    /**
     * Adds XP to the player.
     * @param amt The amount of xp to add
     */
    public void addXp(int amt) {
        Audio.playAudio("xp_gain");
        xp += amt;
        while (xp >= xpNeeded(level)) {
            xp -= xpNeeded(level);
            level++;
            Audio.playAudio("level_up");
        }
        Platform.runLater(() -> GameScreen.getInstance().updateHud());
    }

    public int getLevel() {
        return level;
    }

    public int getXp() {
        return xp;
    }

    /**
     * Calculates how much xp is needed to level up from a specific level.
     * @param level Level to check
     * @return Returns the amount of xp needed to level up to level + 1
     */
    public static int xpNeeded(int level) {
        return (int) Math.pow(level, 1.8) + 100;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject obj = super.saveObject();
        obj.put("gold", gold);
        obj.put("monstersKilled", monstersKilled);
        obj.put("totalDamageDealt", totalDamageDealt);
        obj.put("totalItemsConsumed", totalItemsConsumed);
        JSONArray eff = new JSONArray();
        for (Effect e : effects) {
            eff.put(e.saveObject());
        }
        obj.put("effects", eff);
        obj.put("inventory", inventory.saveObject());
        obj.put("selected", selected);
        obj.put("direction", direction);
        obj.put("level", level);
        obj.put("xp", xp);
        return obj;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }
}
