package dungeoncrawler;

import javafx.scene.input.KeyCode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

class BiMap<K,V> {

    HashMap<K,V> map = new HashMap<K, V>();
    HashMap<V,K> inversedMap = new HashMap<V, K>();

    BiMap(K[] keys, V[] values) {
        for (int i = 0; i < keys.length; i++) {
            put(keys[i], values[i]);
        }
    }

    void put(K k, V v) {
        map.put(k, v);
        inversedMap.put(v, k);
    }

    V get(K k) {
        return map.get(k);
    }

    K getKey(V v) {
        return inversedMap.get(v);
    }

}

public class Controls {
    /*
     * Please note: The use of F13-F24 is reserved for special/mouse keys. Mapping is as follows:
     *      F13 - mouse1/left click
     *      F14 - mouse2/right click
     *      F15 - mouse wheel up
     *      F16 - mouse wheel down
     */
    private static final BiMap<String, KeyCode> mouseKeys = new BiMap<>(
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
     * Constructor.
     * @param file File of the config file. By default it is "config/config.cfg", but field is included for debugging purposes
     */
    public Controls(File file) {
        saveFile = file;

        //If save file exists, then load data from file
        if (file.exists() && !file.isDirectory()) {
            loadConfig(file.getPath());
        }
    }
    public Controls() {
        this(new File("config/config.cfg"));
    }

    /**
     * Loads a config file line-by-line.
     * @param filePath Path of the config file to load.
     */
    private void loadConfig(String filePath) {
        try {
            BufferedReader loader = new BufferedReader(new FileReader(filePath));
            String line = loader.readLine();
            int lineNumber = 1;

            while (line != null) {
                String[] lineData = line.split(" ");

                //check for command
                try {
                    switch (lineData[0]) {
                        case "bind":
                            loadKey(lineData);
                            break;
                        default:
                            System.out.println("Unknown command on line " + lineNumber);
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + " on line " + lineNumber);
                }

                //next line
                line = loader.readLine();
                lineNumber++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handler for the "bind" config command. If a bind is invalid, such as invalid key or control, then it simply does
     * nothing and prints an error. As a result, some controls may not be bound due to typos.
     * @param args Args should be a 3-long String array with elements "bind", [KEY], and [CONTROL]
     */
    private void loadKey(String[] args) {
        //format should be "bind <KEY> <CONTROL>"
        if (args.length != 3) {
            throw new IllegalArgumentException("Invalid command format");
        }
        String key = args[1];
        String control = args[2];
        try {
            setKey(control, key);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    /**
     * Resets controls. Also where all default controls/keymappings are stored.
     * @return Returns true on success
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
        keyMap.put("nextInv", KeyCode.F15);
        keyMap.put("prevInv", KeyCode.F16);
        keyMap.put("slot1", KeyCode.DIGIT1);
        keyMap.put("slot2", KeyCode.DIGIT2);
        keyMap.put("slot3", KeyCode.DIGIT3);
        keyMap.put("slot4", KeyCode.DIGIT4);
        keyMap.put("slot5", KeyCode.DIGIT5);

        //Weapon Controls
        keyMap.put("attack", KeyCode.F13);
        keyMap.put("attack2", KeyCode.F14);
        keyMap.put("reload", KeyCode.R);
    }

    /**
     * Saves keymappings to file. Generates new file if one doesn't exist.
     * @return Returns true on success
     */
    private void save() throws IOException {
        //if file doesn't exist, create it
        if (!saveFile.exists()) {
            saveFile.createNewFile();
        }

        String saveString = "";
        for (Map.Entry<String, KeyCode> e : keyMap.entrySet()) {
            saveString += "\\nbind " + e.getKey() + " " + "v";
        });
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
        KeyCode foundCode = keyMap.get(controlName);
        if (foundCode == null) {
            throw new IllegalArgumentException("No such control.");
        }

        //if the keycode corresponds to a mouse button, then return the mouse button code
        if (mouseKeys.getKey(foundCode) != null) {
            return mouseKeys.getKey(foundCode);
        }
        return foundCode.toString();
    }

    /**
     * Sets the KeyCode for a control.
     * @param controlName String name of the control to change
     * @param key KeyCode to change the control to
     */
    public void setKey(String controlName, String key) {
        if (controlName == null) {
            throw new IllegalArgumentException("Control cannot be null.");
        }
        if (key == null) {
            throw new IllegalArgumentException("Key cannot be null.");
        }

        //get KeyCode object from the key string
        KeyCode code;
        try {
            //if the key is a mousebutton, get the KeyCode corresponding to it.
            if (mouseKeys.get(key) != null) {
                code = mouseKeys.get(key);
            } else {
                code = KeyCode.valueOf(key);
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid key.");
        }

        keyMap.replace(controlName, code);
    }
}
