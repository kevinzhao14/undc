package undc.handlers;

import undc.controllers.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

        //If save file exists, then load data from file
        if (file.exists() && !file.isDirectory()) {
            loadConfig(file.getPath());
        } else { //file doesn't exist, create new file and save
            resetKeys();
            try {
                save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Controls getInstance() {
        if (instance == null) {
            instance = new Controls();
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
            int lineNumber = 1;

            //loop through each line in the file.
            while (line != null) {
                //the command stored on the line
                String[] lineData = line.split(" ");

                //check command validity and act
                try {
                    switch (lineData[0]) {
                    case "": //empty line or bad spacing
                        break;
                    case "bind": //bind key
                        loadKey(lineData);
                        break;
                    default:
                        Console.error("Unknown command on line " + lineNumber + ": '" + lineData[0] + "'");
                    }
                } catch (IllegalArgumentException e) {
                    Console.error(e.getMessage() + " on line " + lineNumber);
                }

                //next line
                line = loader.readLine();
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler for the "bind" config command. If a bind is invalid, such as
     * invalid key or control, then it simply does nothing and prints an error.
     * As a result, some controls may not be bound due to typos.
     * @param args Args should be a 3-long String array with elements "bind", [KEY], and [CONTROL]
     */
    private void loadKey(String[] args) {
        //format should be "bind <KEY> <CONTROL>"
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid command format");
        }
        //<KEY> value and <CONTROL> value
        String key = args[1].toLowerCase();
        String control = args[2].toLowerCase();
        try {
            setKey(key, control);
        } catch (IllegalArgumentException e) {
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

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves keymappings to file. Generates new file if one doesn't exist.
     * @throws IOException Throws exception for any file issues.
     */
    public void save() throws IOException {
        //if file doesn't exist, create it
        if (!saveFile.exists()) {
            //make directory(s) if they don't exist
            saveFile.getParentFile().mkdirs();
            saveFile.createNewFile();
        }

        //generate a string with all the key binds
        String saveString = "";
        for (Map.Entry<String, String> e : keyMap.entrySet()) {
            saveString += "bind " + e.getKey().toLowerCase() + " "
                    + e.getValue().toLowerCase() + "\n";
        }

        //write to file
        BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getPath()));
        writer.write(saveString);
        writer.close();
    }

    /**
     * Returns the KeyCode associated with a control.
     * @param key String name of the control to retrieve the key for
     * @return Returns a String for the specified control to include mouse buttons.
     */
    public String getControl(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Control cannot be null.");
        }
        //get the KeyCode associated with the control name. If null, then the bind doesn't exist.
        String foundControl = keyMap.get(key.toLowerCase());
        if (foundControl == null) {
            return "";
            //throw new IllegalArgumentException("Key is not bound.");
        }
        return foundControl;
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

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
            Console.error("Failed to save properly");
        }
    }
}