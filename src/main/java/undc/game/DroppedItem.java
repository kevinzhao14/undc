package undc.game;

import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.items.Item;
import undc.general.Savable;

/**
 * Class that handles items dropped from a player's inventory.
 */
public class DroppedItem extends GameObject implements Savable {
    private final Item item;

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
        this.z = -100;
        this.width = w;
        this.height = h;
    }

    public Item getItem() {
        return item;
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
