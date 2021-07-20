package undc.entity;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.command.Console;
import undc.general.Savable;

/**
 * Represents dialogue options for an entity.
 */
public class Dialogue implements Savable {
    private final String[] dialogue;
    private final boolean loops;

    private int pos;

    /**
     * Constructor.
     * @param dialogue Lines that compose the dialogue
     * @param loops Whether the dialogue should loop
     */
    public Dialogue(String[] dialogue, boolean loops) {
        this.dialogue = dialogue;
        this.loops = loops;
        this.pos = 0;
    }

    /**
     * Used to get the next line in the dialogue.
     * @return Returns the next line
     */
    public String next() {
        if (pos >= dialogue.length) {
            return "";
        }
        String res = dialogue[pos++];
        if (loops) {
            pos %= dialogue.length;
        }
        return res;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        JSONArray d = new JSONArray();
        for (String s : dialogue) {
            d.put(s);
        }
        o.put("dialogue", d);
        o.put("loops", loops);
        o.put("pos", pos);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            pos = o.getInt("pos");
        } catch (Exception e) {
            Console.error("Failed to load Dialogue.");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into a Dialogue object.
     * @param o Data to load
     * @return The corresponding dialogue object
     */
    public static Dialogue parseSaveObject(JSONObject o) {
        try {
            JSONArray dObj = o.getJSONArray("dialogue");
            String[] dialogue = new String[dObj.length()];
            for (int i = 0; i < dObj.length(); i++) {
                dialogue[i] = dObj.getString(i);
            }
            boolean loops = o.getBoolean("loops");
            return new Dialogue(dialogue, loops);
        } catch (Exception e) {
            Console.error("Failed to create Dialogue.");
            return null;
        }
    }
}
