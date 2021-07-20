package undc.game;

import org.json.JSONObject;
import undc.command.Console;
import undc.general.Movable;
import undc.general.Savable;

/**
 * Represents a floor tile.
 */
public class Floor implements Movable, Savable {
    private final String id;
    private final double x;
    private final double y;
    private final int width;
    private final int height;

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
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {

    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
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

    public String getId() {
        return id;
    }
}
