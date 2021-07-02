package undc.command;

import javafx.scene.image.Image;
import javafx.scene.media.AudioClip;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import undc.general.Audio;
import undc.game.Difficulty;
import undc.item.Item;
import undc.entity.Monster;
import undc.game.Obstacle;
import undc.item.Projectile;
import undc.general.Savable;
import undc.item.Weapon;
import undc.item.Key;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class for storing and handling all session data.
 *
 */
public class DataManager implements Savable {
    public static final HashMap<String, Projectile> PROJECTILES = new HashMap<>();

    public static final HashMap<String, Item> ITEMS = new HashMap<>();

    public static final HashMap<String, Obstacle> OBSTACLES = new HashMap<>();

    public static final HashMap<String, Monster> MONSTERS = new HashMap<>();

    public static final HashMap<String, AudioClip> SOUNDS = new HashMap<>();

    public static final ArrayList<Image> FLOORS = new ArrayList<>();

    public static final int FLOOR_SIZE = 64;

    public static final String EXPLOSION = "textures/boom.gif";

    private static boolean unlockedAmmo = false;
    private static Key exitKey;
    private static Weapon[] startingWeapons;
    private static Monster finalBoss;

    private Difficulty difficulty;
    private Weapon weapon;
    private String name = "Example";
    private File saveFile;

    /**
     * Basic constructor for creating a DataManager.
     */
    public DataManager() {
        difficulty = null;
        weapon = null;
        load();
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
        for (Weapon w : startingWeapons) {
            if (weapon == w) {
                validWeapon = true;
                break;
            }
        }
        if (!validWeapon) {
            throw new IllegalArgumentException("Invalid weapon selection.");
        }

        //save data
        this.difficulty = difficulty;
        double modifier = 1.0;
        if (difficulty == Difficulty.MEDIUM) {
            modifier = Vars.d("sv_modifier_medium");
        } else if (difficulty == Difficulty.HARD) {
            modifier = Vars.d("sv_modifier_hard");
        }
        Vars.find("sv_modifier").setVal(modifier + "", true);
        this.weapon = weapon.copy();
        return true;
    }

    /**
     * Handles saving some save data to a file.
     * @param o Save data to save
     * @return Returns true on success, false on failure
     */
    public boolean saveGame(JSONObject o) {
        if (o == null) {
            Console.error("Invalid save data.");
            return false;
        }
        // new game save, then make new save file
        if (saveFile == null) {
            saveFile = new File("saves/" + name + ".save");
            int counter = 0;
            while (saveFile.exists()) {
                counter++;
                saveFile = new File("saves/" + name + "-" + counter + ".save");
            }
        }
        // make folder if it doesn't exist
        if (!saveFile.getParentFile().exists() && !saveFile.getParentFile().mkdirs()) {
            Console.error("Failed to create save file location.");
            return false;
        }

        // make file if it doesn't exist
        if (!saveFile.exists()) {
            try {
                saveFile.createNewFile();
            } catch (IOException e) {
                Console.error("Failed to create save file.");
                return false;
            }
        }
        // write to file
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getPath()));
            writer.write(o.toString(4));
            writer.close();
        } catch (IOException e) {
            Console.error("Failed to save the game.");
            return false;
        }
        return true;
    }

    /**
     * Getter for the difficulty level.
     * @return The difficulty level
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    public static Key getExitKey() {
        return exitKey;
    }

    public static Weapon[] getStartingWeapons() {
        return startingWeapons;
    }

    public static Monster getFinalBoss() {
        return finalBoss;
    }

    /**
     * Getter for the weapon.
     * @return The weapon
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * Loads static data (items, monsters, obstacles, etc.) from the data file.
     */
    private static void load() {
        String file;
        try {
            file = Files.readString(Paths.get("data/data.json"));
        } catch (IOException e) {
            Console.error("Failed to load items.");
            //TODO: stop game
            return;
        }
        JSONObject obj = new JSONObject(file);
        if (!loadProjectiles(obj) || !loadMonsters(obj) || !loadObstacles(obj) || !loadItems(obj) || !loadSounds(obj)
                || !loadFloors(obj)) {
            //TODO: stop game
        }
    }

    /**
     * Loads all projectiles and their data into Projectile objects.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */
    private static boolean loadProjectiles(JSONObject obj) {
        JSONArray projectiles = obj.getJSONArray("projectiles");
        for (int i = 0; i < projectiles.length(); i++) {
            JSONObject o = projectiles.getJSONObject(i);
            Projectile proj = Projectile.parse(o);
            if (proj == null) {
                return false;
            }
            if (PROJECTILES.containsKey(proj.getId())) {
                Console.error("Duplicate projectile id " + proj.getId());
                return false;
            }
            PROJECTILES.put(proj.getId(), proj);
        }
        return true;
    }

    /**
     * Loads all monsters and their data into Monster objects.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */

    private static boolean loadMonsters(JSONObject obj) {
        JSONArray monsters = obj.getJSONArray("monsters");
        for (int i = 0; i < monsters.length(); i++) {
            JSONObject o = monsters.getJSONObject(i);
            Monster monster = Monster.parse(o);
            if (monster == null) {
                return false;
            }
            if (MONSTERS.containsKey(monster.getId())) {
                Console.error("Duplicate monster id " + monster.getId());
                return false;
            }
            MONSTERS.put(monster.getId(), monster);
        }

        // load the final boss
        try {
            String finalbossid = obj.getString("finalboss");
            if (MONSTERS.get(finalbossid) == null) {
                Console.error("Invalid final boss id.");
                return false;
            }
            finalBoss = MONSTERS.get(finalbossid);
        } catch (JSONException e) {
            Console.error("Invalid value for final boss.");
            return false;
        }
        return true;
    }

    /**
     * Loads all obstacles and their data into Obstacle objects.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */
    private static boolean loadObstacles(JSONObject obj) {
        JSONArray obstacles = obj.getJSONArray("obstacles");
        for (int i = 0; i < obstacles.length(); i++) {
            JSONObject o = obstacles.getJSONObject(i);
            Obstacle obs = Obstacle.parse(o);
            if (obs == null) {
                return false;
            }
            try {
                String id = o.getString("id");
                if (OBSTACLES.containsKey(id)) {
                    Console.error("Duplicate item id " + id);
                    return false;
                }
                OBSTACLES.put(id, obs);
            } catch (JSONException e) {
                Console.error("Invalid value for obstacle id.");
                return false;
            }

        }
        return true;
    }

    /**
     * Loads all items and their data into their respective Item objects.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */
    private static boolean loadItems(JSONObject obj) {
        JSONArray items = obj.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject o = items.getJSONObject(i);
            Item item = Item.parse(o);
            if (item == null) {
                return false;
            }
            if (ITEMS.containsKey(item.getId())) {
                Console.error("Duplicate item id " + item.getId());
                return false;
            }
            ITEMS.put(item.getId(), item);
        }

        // load the exit key
        try {
            String exitkeyid = obj.getString("exitkey");
            if (!(ITEMS.get(exitkeyid) instanceof Key)) {
                Console.error("Invalid type for exit key.");
                return false;
            }
            exitKey = (Key) ITEMS.get(exitkeyid);
        } catch (JSONException e) {
            Console.error("Invalid value for exit key.");
            return false;
        }

        // load the starting weapons
        JSONArray sw;
        try {
            sw = obj.getJSONArray("startingWeapons");
        } catch (JSONException e) {
            Console.error("Invalid value for starting weapons.");
            return false;
        }
        Weapon[] weapons = new Weapon[sw.length()];
        for (int i = 0; i < sw.length(); i++) {
            try {
                String wid = sw.getString(i);
                if (!(ITEMS.get(wid) instanceof Weapon)) {
                    Console.error("Invalid type for starting weapon " + i + ".");
                    return false;
                }
                Weapon weapon = (Weapon) ITEMS.get(wid);
                weapons[i] = weapon;
            } catch (JSONException e) {
                Console.error("Invalid starting weapon " + i + ".");
                return false;
            }
        }
        startingWeapons = weapons;

        return true;
    }

    /**
     * Loads all sounds and their mp3s.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */
    private static boolean loadSounds(JSONObject obj) {
        JSONArray sounds = obj.getJSONArray("sounds");
        for (int i = 0; i < sounds.length(); i++) {
            // get JSONObjects out of JSONArray
            JSONObject o = sounds.getJSONObject(i);
            Audio audio = Audio.parse(o);
            if (audio == null) {
                return false;
            }
            // put key and clip into SOUNDS HashMap
            if (SOUNDS.containsKey(audio.getId())) {
                Console.error("Duplicate audio id " + audio.getId());
                return false;
            }
            SOUNDS.put(audio.getId(), audio.getClip());
        }
        // Set up properties for AudioClips that require slight altering such as making them repeat indefinitely or
        // adjusting play rate
        SOUNDS.get("menu").setCycleCount(AudioClip.INDEFINITE);
        return true;
    }

    /**
     * Loads all floor sprites into images.
     * @param obj JSON object to load from
     * @return Returns true if successful, false otherwise
     */
    private static boolean loadFloors(JSONObject obj) {
        JSONArray floors = obj.getJSONArray("floors");
        for (int i = 0; i < floors.length(); i++) {
            try {
                Image img = new Image(floors.getString(i));
                FLOORS.add(img);
            } catch (JSONException e) {
                Console.error("Invalid value for floor.");
                return false;
            }
        }
        return true;
    }

    @Override
    public JSONObject saveObject() {
        return null;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }
}
