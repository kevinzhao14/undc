package com.luckless.dungeoncrawler;

/**
 * Class for storing and handling all session data.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class DataManager {
    private String username;
    private int difficulty;
    private int weapon;

    /**
     * Basic constructor for creating a DataManager.
     */
    public DataManager() {
        username = "";
        difficulty = -1;
        weapon = -1;
    }

    /**
     * Method for handling data from the initial configuration.
     * @param username Username of the player
     * @param difficulty Difficulty level selected by the player
     * @param weapon Starting weapon selected by the player.
     * @return Returns true if data is valid and saved successfully. Otherwise, false
     */
    public boolean newGame(String username, int difficulty, int weapon) {
        //Checks for empty/whitespace-only username
        if (username == null || username.replaceAll("\\s", "").length() == 0) {
            return false;
        }

        /* Possible values for difficulty:
         * 0 - Easy
         * 1 - Medium
         * 2 - Hard
         */
        if (difficulty < 0 || difficulty > 2) {
            return false;
        }

        /* Possible values for weapon:
         * 0 -
         * 1 -
         * 2 -
         */
        if (weapon < 0 || weapon > 2) {
            return false;
        }

        //save data
        this.username = username;
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
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Getter for the weapon.
     * @return The weapon
     */
    public int getWeapon() {
        return weapon;
    }
}
