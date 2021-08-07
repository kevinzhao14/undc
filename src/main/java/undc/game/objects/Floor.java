package undc.game.objects;

import org.json.JSONObject;
import undc.command.Console;
import undc.general.Savable;

/**
 * Represents a floor tile.
 */
public class Floor extends GameObject implements Savable {
    private final String id;

    /**
     * Constructor.
     * @param id ID of the sprite to use
     * @param width Width of the floor
     * @param height Height of the floor
     * @param x X position of the floor
     * @param y Y position of the floor
     */
    public Floor(String id, int width, int height, double x, double y) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.z = -1000;
    }

    public String getId() {
        return id;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("x", x);
        o.put("y", y);
        o.put("width", width);
        o.put("height", height);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        return true;
    }

    /**
     * Loads save data into a Floor object.
     * @param o The data to load
     * @return The corresponding Floor object
     */
    public static Floor parseSaveObject(JSONObject o) {
        try {
            String id = o.getString("id");
            double x = o.getDouble("x");
            double y = o.getDouble("y");
            int width = o.getInt("width");
            int height = o.getInt("height");
            return new Floor(id, width, height, x, y);
        } catch (Exception e) {
            Console.error("Failed to create floor.");
            return null;
        }
    }
}
