package dungeoncrawler.controllers;

import dungeoncrawler.handlers.Difficulty;
import dungeoncrawler.handlers.GameSettings;
import dungeoncrawler.objects.Bomb;
import dungeoncrawler.objects.Item;
import dungeoncrawler.objects.Monster;
import dungeoncrawler.objects.MonsterType;
import dungeoncrawler.objects.Potion;
import dungeoncrawler.objects.PotionType;
import dungeoncrawler.objects.Weapon;

/**
 * Class for storing and handling all session data.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class DataManager {
    public static final Weapon[] WEAPONS = new Weapon[]{
        new Weapon("Axe", "weapons/axe.png", 16, 1, false),
        new Weapon("Mace", "weapons/mace.png", 12, 0.75, false),
        new Weapon("Sword", "weapons/sword.png", 8, 0.5, false)
    };

    public static final Item[] ITEMS = new Item[]{
        new Potion("Small Health Potion", "items/health-potion-small.png", 10, true,
                PotionType.HEALTH, 25),
        new Potion("Medium Health Potion", "items/health-potion-med.png", 10, true,
                PotionType.HEALTH, 50),
        new Potion("Large Health Potion", "items/health-potion-large.png", 10, true,
                PotionType.HEALTH, 100),
        new Potion("Attack Potion", "items/attack-potion.png", 10, true, PotionType.ATTACK, 0.25),
        new Weapon("Dagger", "items/dagger.png", 5, 0.25, true),
        new Bomb("Bomb", "items/bomb.png", 10, 50, 50, 3000)
    };

    public static final Monster[] MONSTERS = new Monster[]{
        new Monster(20, 4, 150.0 / GameSettings.FPS, 0.5, MonsterType.FAST, 11, 9),
        new Monster(40, 5, 100.0 / GameSettings.FPS, 0.75, MonsterType.NORMAL, 24, 12),
        new Monster(80, 10, 50.0 / GameSettings.FPS, 2, MonsterType.TANK, 21, 31)
    };

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
}
