package dungeoncrawler.controllers;

import dungeoncrawler.handlers.Difficulty;
import dungeoncrawler.objects.Weapon;

/**
 * Class for storing and handling all session data.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class DataManager {
    public static final Weapon[] WEAPONS = new Weapon[]{
        new Weapon("Axe", "axe.png", 8, 1),
        new Weapon("Sword", "sword.png", 4, 0.5),
        new Weapon("Mace", "mace.png", 6, 0.75)
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
        this.weapon = weapon;
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