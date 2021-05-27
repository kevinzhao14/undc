package undc.handlers;

import undc.controllers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Class for managing and saving/loading user configuration data.
 * @version 1.0
 * @author Kevin Zhao
 */
public class Controls {
    public static Controls instance;

    private File saveFile;
    private HashMap<String, String> keyMap = new HashMap<>();

    /**
     * Constructor for a Controls config object.
     * @param file File for the save file.
     */
    private Controls(File file) {
        saveFile = file;
    }

    public static Controls getInstance() {
        if (instance == null) {
            instance = new Controls();
            //If save file exists, then load data from file
            if (instance.saveFile.exists() && !instance.saveFile.isDirectory()) {
                instance.loadConfig(instance.saveFile.getPath());
            } else { //file doesn't exist, create new file and save
                instance.resetKeys();
                instance.save();
            }
        }
        return instance;
    }

    /**
     * Default Constructor for a Controls object. Sets save file to config/config.cfg.
     */
    public Controls() {
        this(new File("config/config.cfg"));
    }

    /**
     * Loads a config file's data into the object data.
     * @param filePath Path of the config file to load
     */
    private void loadConfig(String filePath) {
        try {
            //file reader for reading the save file line by line
            BufferedReader loader = new BufferedReader(new FileReader(filePath));
            String line = loader.readLine();

            //loop through each line in the file.
            while (line != null) {
                //check command validity and act
                Console.run(line, false, true);

                //next line
                line = loader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets controls. Also where all default controls/keymappings are stored.
     */
    public void resetKeys() {
        keyMap.clear();

        //Movement Controls
        keyMap.put("w", "up");
        keyMap.put("s", "down");
        keyMap.put("a", "left");
        keyMap.put("d", "right");
        keyMap.put("shift", "sprint");

        //Interaction Controls
        keyMap.put("e", "interact");
        keyMap.put("space", "use");
        keyMap.put("tab", "map");
        keyMap.put("escape", "pause");
        keyMap.put("i", "inventory");
        keyMap.put("g", "drop");
        keyMap.put("back_quote", "console");

        //Inventory Controls
        keyMap.put("mwheeldown", "nextinv");
        keyMap.put("mwheelup", "previnv");
        keyMap.put("digit1", "slot1");
        keyMap.put("digit2", "slot2");
        keyMap.put("digit3", "slot3");
        keyMap.put("digit4", "slot4");
        keyMap.put("digit5", "slot5");

        //Weapon Controls
        keyMap.put("mouse1", "attack");
        keyMap.put("mouse2", "attack2");
        keyMap.put("f", "rotateinv");
        keyMap.put("r", "reload");

        save();
    }

    /**
     * Saves keymappings to file. Generates new file if one doesn't exist.
     */
    public void save() {
        //if file doesn't exist, create it
        try {
            if (!saveFile.exists()) {
                //make directory(s) if they don't exist
                if (!saveFile.getParentFile().mkdirs()) {
                    Console.error("Failed to create config directory.");
                    return;
                }
                if (!saveFile.createNewFile()) {
                    Console.error("Failed to create config file.");
                    return;
                }
            }

            //generate a string with all the key binds
            StringBuilder saveString = new StringBuilder();
            for (Map.Entry<String, String> e : keyMap.entrySet()) {
                saveString.append("bind ").append(e.getKey().toLowerCase()).append(" ").append(e.getValue().toLowerCase()).append("\n");
            }

            //write to file
            BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getPath()));
            writer.write(saveString.toString());
            writer.close();
        } catch(IOException e) {
            Console.error("Failed to save controls.");
        }
    }

    /**
     * Returns the KeyCode associated with a control.
     * @param key String name of the control to retrieve the key for
     * @return Returns a String for the specified control to include mouse buttons.
     */
    public String getControl(String key) {
        if (key == null) {
            Console.error("Control cannot be null.");
            return "";
        }
        //get the KeyCode associated with the control name. If null, then the bind doesn't exist.
        String foundControl = keyMap.get(key.toLowerCase());
        //Console.error("Key is not bound.");
        return Objects.requireNonNullElse(foundControl, "");
    }

    /**
     * Prints the key mapping.
     */
    public void printMapping() {
        Console.print("Printing size " + keyMap.size());
        keyMap.forEach((k, v) -> Console.print(k + ", " + v));
    }

    /**
     * Sets the KeyCode for a control.
     * @param key KeyCode to change the control to
     * @param control String name of the control to change
     */
    public void setKey(String key, String control) {
        if (control == null) {
            Console.error("Control cannot be null.");
            return;
        }
        if (key == null) {
            Console.error("Key cannot be null.");
            return;
        }

        //if the control is already mapped, overwrite it
        keyMap.put(key, control);

        save();
    }

    public void removeKey(String key) {
        if (key == null) {
            Console.error("Key cannot be null.");
            return;
        }
        if (keyMap.get(key) == null) {
            Console.error("Key is not bound.");
            return;
        }
        keyMap.remove(key);
        save();
    }
}