package undc.entity;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.game.GameController;
import undc.general.Controller;
import undc.command.DataManager;
import undc.items.Ammunition;
import undc.game.ChallengeRoom;
import undc.inventory.InventoryItem;
import undc.items.Item;
import undc.game.calc.Move;
import undc.graphics.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.general.Audio;
import undc.command.Vars;

import java.util.ArrayList;
import java.util.Random;

/**
 * Implementation of the Monster class.
 */
public class Monster extends Entity {
    private final ArrayList<Move> moveQueue;

    private String id;
    private String name;
    private MonsterType type;
    private double speed;
    private double attackSpeed;

    // status variables, changed through the game
    private double reaction;
    private double opacity;

    /**
     * Creates a Monster object.
     */
    private Monster() {
        reaction = -1;
        moveQueue = new ArrayList<>();
        opacity = 1;
    }

    /**
     * Creates a copy of a Monster with modified health and attack stats.
     * @param modifier double to be applied to health and attack stats
     * @return Monster that is modified
     */
    public Monster copy(double modifier) {
        Monster m = new Monster();
        copy(m);
        m.maxHealth = (int) (maxHealth * modifier);
        m.health = m.maxHealth;
        m.attack = attack * modifier;
        m.id = this.id;
        m.name = this.name;
        m.type = this.type;
        m.speed = this.speed;
        m.attackSpeed = this.attackSpeed;
        return m;
    }

    /**
     * Updates health of monster after it receives damage and gives player gold for killing it.
     * @param damageAmount double amount of damage taken.
     */
    public void attackMonster(double damageAmount) {
        if (this.getHealth() <= 0) {
            Console.error("Cannont attack slain monster.");
            return;
        }
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Invalid State.");
            return;
        }
        GameScreen screen = GameScreen.getInstance();

        // update damage dealt stat in player object
        screen.getPlayer().addDamageDealt(Math.min(this.getHealth(), damageAmount));

        // play attack sound
        Audio.playAudio("attack");

        // change monster health
        health = Math.max(0, this.getHealth() - damageAmount);

        // Give gold to player after slaying a monster
        if (health == 0) {
            // give gold
            screen.getPlayer().setGold(screen.getPlayer().getGold() + Vars.i("sv_monster_gold"));

            // add xp
            GameScreen.getInstance().getPlayer().addXp(Vars.i("sv_monster_xp"));

            // update number of monsters player killed
            screen.getPlayer().addMonsterKilled();

            // make monster disappear
            opacity = 1 - (1000.0 / Vars.i("sv_tickrate") / Vars.i("gc_monster_fade_dur"));

            // only drop items if it's not a challenge room
            if (!(screen.getRoom() instanceof ChallengeRoom)) {
                // generate drop items and add to Room ArrayList
                dropItems();
            } else { // drop the rewards if challenge room is complete
                boolean allDead = true;
                for (Entity e : screen.getRoom().getEntities()) {
                    if (e instanceof Monster && e.getHealth() > 0) {
                        allDead = false;
                        break;
                    }
                }
                if (allDead) {
                    ((ChallengeRoom) screen.getRoom()).openDoors();
                    ((ChallengeRoom) screen.getRoom()).setCompleted(true);
                    Player player = screen.getPlayer();

                    // drop items
                    for (InventoryItem item : ((ChallengeRoom) screen.getRoom()).getRewards()) {
                        if (item.getItem().equals(DataManager.ITEMS.get("rocket_launcher"))) {
                            DataManager.getInstance().setUnlockedAmmo(true);
                        }
                        for (int i = 0; i < item.getQuantity(); i++) {
                            double x = player.getX() + player.getWidth() / 2.0;
                            double y = player.getY() + player.getHeight() / 2.0;
                            GameController.getInstance().dropAt(item.getItem(), x, y);
                        }
                    }
                }
            }

            //use run later to prevent any thread issues
            Platform.runLater(screen::updateHud);
        }
    }

    /**
     * Restores the monster's health points to full and resets their position.
     * @param posX int x-chord to reset player to
     * @param posY int y-chord to reset player to
     */
    public void revive(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        health = maxHealth;
        opacity = 1;
    }

    /**
     * Handles dropping items for the player to receive upon killing a monster.
     */
    private void dropItems() {
        if (DataManager.ITEMS.size() == 0 && type != MonsterType.FINALBOSS) {
            Console.error("No items available to drop.");
            return;
        }
        GameController gc = GameController.getInstance();

        // Drop final boss key
        if (type == MonsterType.FINALBOSS) {
            if (Audio.getAudioClip("final_boss_music").isPlaying()) {
                Audio.getAudioClip("final_boss_music").stop();
            }
            Audio.playAudio("boss_defeat");
            Item key = DataManager.getExitKey();
            Image sprite = key.getSprite();
            double x = posX + width / 2.0 - sprite.getWidth() / 2;
            double y = posY + height / 2.0 - sprite.getHeight() / 2;
            gc.dropAt(key, x, y);
            return;
        }
        ArrayList<Item> items = new ArrayList<>(DataManager.ITEMS.values());

        Random generator = new Random();
        //Calculate number of items to drop
        int min = Vars.i("sv_itemdrop_min");
        int max = Vars.i("sv_itemdrop_max");
        int numItems = generator.nextInt(max - min + 1) + min;

        // Maximum distance between monster death location and item spawn location
        double maxRadius = 1.5 * getWidth();

        for (int i = 0; i < numItems; i++) {
            // keep generating a new item until a droppable item is found
            Item item;
            do {
                item = items.get(generator.nextInt(items.size()));
            } while (!item.isDroppable()
                    || (item instanceof Ammunition && !DataManager.getInstance().isUnlockedAmmo()));

            // calculate x and y
            double randDist = maxRadius * generator.nextDouble();
            double randAngle = 2 * Math.PI * generator.nextDouble();
            double x = getX() + (randDist * Math.cos(randAngle));
            double y = getY() + (randDist * Math.sin(randAngle));

            gc.dropAt(item, x, y);
        }
    }

    public double getReaction() {
        return reaction;
    }

    public void setReaction(double reaction) {
        this.reaction = reaction;
    }

    public double getSpeed() {
        return speed;
    }

    public ArrayList<Move> getMoveQueue() {
        return moveQueue;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public MonsterType getType() {
        return this.type;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Type: " + type + " | Speed: " + speed + " | Attack: "
                + attack + " " + attackSpeed + " " + super.toString();
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("health", health);
        o.put("posX", posX);
        o.put("posY", posY);
        o.put("attackCooldown", attackCooldown);
        o.put("reaction", reaction);
        o.put("opacity", opacity);
        o.put("class", "Monster");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            health = o.getDouble("health");
            posX = o.getDouble("posX");
            posY = o.getDouble("posY");
            attackCooldown = o.getDouble("attackCooldown");
            reaction = o.getDouble("reaction");
            opacity = o.getDouble("opacity");
        } catch (Exception e) {
            Console.error("Failed to load Monster.");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into a Monster object.
     * @param o The data to load
     * @return The corresponding Monster object
     */
    public static Monster parseSaveObject(JSONObject o) {
        try {
            String id = o.getString("id");
            return DataManager.MONSTERS.get(id);
        } catch (Exception e) {
            Console.error("Failed to create Monster.");
            return null;
        }
    }

    /**
     * Assigns a monster's instance variables from JSON files.
     * @param o JSONObject that info on the monster is pulled from
     * @return Monster object that is updated
     */
    public static Monster parse(JSONObject o) {
        Monster monster = new Monster();
        try {
            monster.id = o.getString("id");
        } catch (JSONException e) {
            Console.error("Invalid value for monster id.");
            return null;
        }
        try {
            monster.name = o.getString("name");
        } catch (JSONException e) {
            Console.error("Invalid value for monster name.");
            return null;
        }
        try {
            monster.maxHealth = o.getInt("health");
            monster.health = monster.maxHealth;
        } catch (JSONException e) {
            Console.error("Invalid value for monster health.");
            return null;
        }
        try {
            monster.attack = o.getDouble("attack");
        } catch (JSONException e) {
            Console.error("Invalid value for monster attack.");
            return null;
        }
        try {
            monster.attackSpeed = o.getDouble("attackSpeed");
        } catch (JSONException e) {
            Console.error("Invalid value for monster attack speed.");
            return null;
        }
        try {
            monster.speed = o.getDouble("speed");
        } catch (JSONException e) {
            Console.error("Invalid value for monster speed.");
            return null;
        }
        try {
            monster.width = o.getInt("width");
        } catch (JSONException e) {
            Console.error("Invalid value for monster width.");
            return null;
        }
        try {
            monster.height = o.getInt("height");
        } catch (JSONException e) {
            Console.error("Invalid value for monster height.");
            return null;
        }
        try {
            monster.type = MonsterType.valueOf(o.getString("type").toUpperCase());
        } catch (JSONException e) {
            Console.error("Invalid value for monster type.");
            return null;
        } catch (IllegalArgumentException a) {
            Console.error("Invalid type for monster type.");
            return null;
        }
        try {
            monster.sprite = new Image(o.getString("sprite"));
        } catch (IllegalArgumentException e) {
            Console.error("Invalid value for monster sprite.");
            return null;
        }
        return monster;
    }
}
