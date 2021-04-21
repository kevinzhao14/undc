package dungeoncrawler.handlers;

import javafx.scene.input.KeyCode;

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
    /*
     * Please note: The use of F13-F24 is reserved for special/mouse keys. Mapping is as follows:
     *      F13 - mouse1/left click
     *      F14 - mouse2/right click
     *      F15 - mouse wheel up
     *      F16 - mouse wheel down
     */
    private static final BiMap<String, KeyCode> MOUSEKEYS = new BiMap<>(
            new String[]{
                "MOUSE1",
                "MOUSE2",
                "MWHEELUP",
                "MWHEELDOWN"
            },
            new KeyCode[]{
                KeyCode.F13,
                KeyCode.F14,
                KeyCode.F15,
                KeyCode.F16
            }
    );

    private File saveFile;
    private HashMap<String, KeyCode> keyMap = new HashMap<>();

    /**
     * Constructor for a Controls config object.
     * @param file File for the save file.
     */
    public Controls(File file) {
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
                        System.out.println("Unknown command on line "
                                + lineNumber + ": \"" + lineData[0] + "\"");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + " on line " + lineNumber);
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
        String key = args[1].toUpperCase();
        String control = args[2].toLowerCase();
        try {
            setKey(control, key);
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
        keyMap.put("up", KeyCode.W);
        keyMap.put("down", KeyCode.S);
        keyMap.put("left", KeyCode.A);
        keyMap.put("right", KeyCode.D);
        keyMap.put("sprint", KeyCode.SHIFT);

        //Interaction Controls
        keyMap.put("interact", KeyCode.E);
        keyMap.put("use", KeyCode.SPACE);
        keyMap.put("map", KeyCode.TAB);
        keyMap.put("pause", KeyCode.ESCAPE);
        keyMap.put("inventory", KeyCode.I);
        keyMap.put("drop", KeyCode.G);

        //Inventory Controls
        keyMap.put("nextinv", KeyCode.F16);
        keyMap.put("previnv", KeyCode.F15);
        keyMap.put("slot1", KeyCode.DIGIT1);
        keyMap.put("slot2", KeyCode.DIGIT2);
        keyMap.put("slot3", KeyCode.DIGIT3);
        keyMap.put("slot4", KeyCode.DIGIT4);
        keyMap.put("slot5", KeyCode.DIGIT5);

        //Weapon Controls
        keyMap.put("attack", KeyCode.F13);
        keyMap.put("attack2", KeyCode.F14);
        keyMap.put("rotateinv", KeyCode.F);
        keyMap.put("reload", KeyCode.R);

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
        for (Map.Entry<String, KeyCode> e : keyMap.entrySet()) {
            saveString += "bind " + e.getValue().toString().toLowerCase() + " "
                    + e.getKey().toLowerCase() + "\n";
        }

        //write to file
        BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile.getPath()));
        writer.write(saveString);
        writer.close();
    }

    /**
     * Returns the KeyCode associated with a control.
     * @param controlName String name of the control to retrieve the key for
     * @return Returns a String for the specified control to include mouse buttons.
     */
    public String getKey(String controlName) {
        if (controlName == null) {
            throw new IllegalArgumentException("Control cannot be null.");
        }
        //get the KeyCode associated with the control name. If null, then the bind doesn't exist.
        KeyCode foundCode = keyMap.get(controlName);
        if (foundCode == null) {
            throw new IllegalArgumentException("No such control.");
        }

        //if the keycode corresponds to a mouse button, then return the mouse button code
        if (MOUSEKEYS.getKey(foundCode) != null) {
            //note the "key" in "getKey" refers to key in "key value pair", not "keycode"
            return MOUSEKEYS.getKey(foundCode);
        }
        return foundCode.toString();
    }

    /**
     * Prints the key mapping.
     */
    public void printMapping() {
        System.out.println("Printing size " + keyMap.size());
        keyMap.forEach((k, v) -> System.out.println(k + ", " + v));
    }

    /**
     * Sets the KeyCode for a control.
     * @param controlName String name of the control to change
     * @param key KeyCode to change the control to
     */
    public void setKey(String controlName, String key) {
        if (controlName == null) {
            System.out.println("Control cannot be null.");
            return;
        }
        if (key == null) {
            System.out.println("Key cannot be null.");
            return;
        }

        //get KeyCode object from the key string
        KeyCode code;
        try {
            //if the key is a mousebutton, get the KeyCode corresponding to it.
            if (MOUSEKEYS.get(key) != null) {
                code = MOUSEKEYS.get(key);
            } else {
                code = KeyCode.valueOf(key);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key.");
        }

        //if the key is already mapped to another control, throw an exception
        if (keyMap.get(controlName) != code && keyMap.containsValue(code)) {
            System.out.println("That key is already mapped to another control.");
            return;
        }
        //if the control is already mapped, overwrite it
        if (keyMap.containsKey(controlName)) {
            keyMap.replace(controlName, code);
        } else {
            keyMap.put(controlName, code);
        }

        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save properly");
        }
    }
}