package undc.game;

import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.items.Item;
import undc.general.Movable;
import undc.general.Savable;

/**
 * Class that handles items dropped from a player's inventory.
 */
public class DroppedItem implements Movable, Savable {
    private final Item item;

    private double x;
    private double y;
    private int width;
    private int height;

    /**
     * Constructor for an item that is dropped, taking in its location, height, and width.
     * @param item Item that is dropped
     * @param x int x-position
     * @param y int y-position
     * @param w int width of the item
     * @param h int height fo the item
     */
    public DroppedItem(Item item, double x, double y, int w, int h) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public DroppedItem(Item item) {
        this(item, 0.0, 0.0, 0, 0);
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(int w) {
        this.width = w;
    }

    public void setHeight(int h) {
        this.height = h;
    }

    public Item getItem() {
        return item;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("item", item.getId());
        o.put("x", x);
        o.put("y", y);
        o.put("width", width);
        o.put("height", height);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            x = o.getDouble("x");
            y = o.getDouble("y");
            width = o.getInt("width");
            height = o.getInt("height");
        } catch (Exception e) {
            Console.error("Failed to load DroppedItem");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into a DroppedItem object.
     * @param o The data to load
     * @return The corresponding DroppedItem object
     */
    public static DroppedItem parseSaveObject(JSONObject o) {
        try {
            Item item = DataManager.ITEMS.get(o.getString("item"));
            return new DroppedItem(item, 0, 0, 0, 0);
        } catch (Exception e) {
            Console.error("Failed to create Dropped Item.");
            return null;
        }
    }
}
