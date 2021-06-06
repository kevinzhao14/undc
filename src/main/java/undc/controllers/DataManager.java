package undc.controllers;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.handlers.Difficulty;
import undc.handlers.GameSettings;
import undc.objects.Ammunition;
import undc.objects.Bomb;
import undc.objects.Item;
import undc.objects.Monster;
import undc.objects.MonsterType;
import undc.objects.Obstacle;
import undc.objects.ObstacleType;
import undc.objects.Potion;
import undc.objects.PotionType;
import undc.objects.Projectile;
import undc.objects.RangedWeapon;
import undc.objects.Weapon;
import undc.objects.Key;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for storing and handling all session data.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class DataManager {
//    public static final Weapon[] WEAPONS = new Weapon[]{
//        new Weapon("Axe", "weapons/axe.png", 16, 1, false),
//        new Weapon("Mace", "weapons/mace.png", 12, 0.75, false),
//        new Weapon("Sword", "weapons/sword.png", 8, 0.5, false)
//    };

    public static final Projectile[] PROJECTILES = new Projectile[] {
        new Projectile("Rocket",
                new String[]{"weapons/rocket-left.png", "weapons/rocket-up.png",
                    "weapons/rocket-right.png", "weapons/rocket-down.png"},
                25, 500.0, 400, true, 25)};

    public static final HashMap<Integer, Item> ITEMS = new HashMap<>();

    public static final Obstacle[] OBSTACLES = new Obstacle[] {
        new Obstacle("obstacles/boulders.png", 0, 0, 23, 17, ObstacleType.SOLID),
        new Obstacle("obstacles/ruin1.png", 0, 0, 10, 11, ObstacleType.SOLID),
        new Obstacle("obstacles/ruin2.png", 0, 0, 31, 18, ObstacleType.SOLID)
    };

    public static final Key EXITKEY = new Key("Special Key", "items/key.png", true);

    public static final Monster[] MONSTERS = new Monster[]{
        new Monster(20, 4, 150.0, 0.5, MonsterType.FAST, 11, 9),
        new Monster(40, 5, 100.0, 0.75, MonsterType.NORMAL, 24, 12),
        new Monster(80, 10, 50.0, 2, MonsterType.TANK, 21, 31)
    };

    public static final Monster FINALBOSS = new Monster(200, 15, 100.0, 1.0,
            MonsterType.FINALBOSS, 48, 48);
    public static final String EXPLOSION = "textures/boom.gif";

    private static boolean unlockedAmmo = false;

    private String username;
    private Difficulty difficulty;
    private Weapon weapon;

    /**
     * Basic constructor for creating a DataManager.
     */
    public DataManager() {
        username = "";
        difficulty = null;
        weapon = null;
    }

    public static boolean isUnlockedAmmo() {
        return unlockedAmmo;
    }

    public static void setUnlockedAmmo(boolean unlockedAmmo) {
        DataManager.unlockedAmmo = unlockedAmmo;
    }

    /**
     * Method for handling data from the initial configuration.
     * @param username Username of the player
     * @param difficulty Difficulty level selected by the player
     * @param weapon Starting weapon selected by the player.
     * @return Returns true if data is valid and saved successfully. Otherwise, false
     * @throws IllegalArgumentException Throws Exception if any field is invalid.
     */
    public boolean newGame(String username, Difficulty difficulty, Weapon weapon) {
        //Checks for empty/whitespace-only username
        if (username == null || username.replaceAll("\\s", "").length() == 0) {
            throw new IllegalArgumentException("Username cannot be empty.");
        }

        /* Possible values for difficulty:
         * 0 - Easy
         * 1 - Medium
         * 2 - Hard
         */

        if (difficulty == null) {
            throw new IllegalArgumentException("Difficulty must be Easy, Medium, or Hard.");
        }

        /* Possible values for weapon:
         * 0 -
         * 1 -
         * 2 -
         */
        boolean validWeapon = false;
        for (Weapon w : WEAPONS) {
            if (weapon == w) {
                validWeapon = true;
                break;
            }
        }
        if (!validWeapon) {
            throw new IllegalArgumentException("Invalid weapon selection.");
        }

        //save data
        this.username = username.replaceAll("\\s{2,}", " ").trim();
        this.difficulty = difficulty;
        this.weapon = weapon.copy();
        return true;
    }

    /**
     * Getter for the player's username.
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the difficulty level.
     * @return The difficulty level
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Getter for the weapon.
     * @return The weapon
     */
    public Weapon getWeapon() {
        return weapon;
    }

    public static void load() {

    }

    public static boolean loadItems() {
        String file;
        try {
            file = Files.readString(Paths.get("data/items.json"));
        } catch (IOException e) {
            Console.error("Failed to load items.");
            return false;
        }
        JSONObject obj = new JSONObject(file);
        JSONArray items = obj.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject o = items.getJSONObject(i);
            Item item = Item.parse(o);
            if (item == null) {
                return false;
            }
            if (ITEMS.containsKey(item.getId())) {
                Console.error("Invalid item ID: " + item.getId());
                return false;
            }
            ITEMS.put(item.getId(), item);
        }
        return true;
    }
}
