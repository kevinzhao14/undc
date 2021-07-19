package undc.entity;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import undc.game.calc.Direction;
import undc.game.Effect;
import undc.inventory.Inventory;
import undc.inventory.InventoryItem;
import undc.general.Savable;
import undc.graphics.SpriteGroup;
import undc.items.Weapon;
import undc.graphics.GameScreen;
import undc.general.Audio;
import undc.command.Vars;

import java.util.ArrayList;

/**
 * Class that handles the main character of the game controlled by the person playing the game.
 */
public class Player extends Entity implements Savable {
    private static final SpriteGroup STANDING_SPRITES = new SpriteGroup(
        new Image("entities/player/player-left.png"),
        new Image("entities/player/player-up.png"),
        new Image("entities/player/player-right.png"),
        new Image("entities/player/player-down.png")
    );

    private static final SpriteGroup MOVING_SPRITES = new SpriteGroup(
        new Image("entities/player/player-walk-left.gif"),
        new Image("entities/player/player-walk-up.gif"),
        new Image("entities/player/player-walk-right.gif"),
        new Image("entities/player/player-walk-down.gif")
    );

    private int gold;
    private int monstersKilled;
    private double totalDamageDealt;
    private int totalItemsConsumed;
    private final ArrayList<Effect> effects;

    private Inventory inventory;
    private int selected;
    private Direction direction;
    private int level;
    private int xp;
    private double walkCooldown;

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
        direction = Direction.SOUTH;
        effects = new ArrayList<>();
        walkCooldown = 0;
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

    public void setDirection(Direction dir, boolean moving) {
        this.setSprite(moving ? MOVING_SPRITES.get(dir) : STANDING_SPRITES.get(dir));
        direction = dir;
    }

    public void setDirection(Direction dir) {
        setDirection(dir, false);
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

    public Direction getDirection() {
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
        JSONObject o = new JSONObject();
        o.put("health", health);
        o.put("posX", posX);
        o.put("posY", posY);
        o.put("attackCooldown", attackCooldown);

        o.put("gold", gold);
        o.put("monstersKilled", monstersKilled);
        o.put("totalDamageDealt", totalDamageDealt);
        o.put("totalItemsConsumed", totalItemsConsumed);
        JSONArray eff = new JSONArray();
        for (Effect e : effects) {
            eff.put(e.saveObject());
        }
        o.put("effects", eff);
        o.put("inventory", inventory.saveObject());
        o.put("selected", selected);
        o.put("direction", direction.toString());
        o.put("level", level);
        o.put("xp", xp);
        o.put("class", "Player");
        return o;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }

    public double getWalkCooldown() {
        return walkCooldown;
    }

    public void setWalkCooldown(double walkCooldown) {
        this.walkCooldown = walkCooldown;
    }
}
