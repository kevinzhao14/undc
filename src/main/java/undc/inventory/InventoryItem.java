package undc.inventory;

import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.items.Item;
import undc.general.Savable;

/**
 * Class that handles Item storage in an Inventory.
 */
public class InventoryItem implements Savable {
    private String item;
    private int quantity;
    private boolean infinite;

    /**
     * Constructor.
     * @param item Item
     * @param quantity Quantity
     */
    public InventoryItem(String item, int quantity) {
        if (DataManager.ITEMS.get(item) == null) {
            Console.error("Invalid item.");
            return;
        }
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return DataManager.ITEMS.get(item);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isInfinite() {
        return infinite;
    }

    public void setInfinite(boolean infinite) {
        this.infinite = infinite;
    }

    public String toString() {
        return "Item: " + item + " | Q: " + quantity;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("item", item);
        o.put("quantity", quantity);
        o.put("infinite", infinite);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            item = o.getString("item");
            if (DataManager.ITEMS.get(item) == null) {
                Console.error("Invalid item.");
                return false;
            }
            quantity = o.getInt("quantity");
            infinite = o.getBoolean("infinite");
        } catch (Exception e) {
            Console.error("Failed to load InventoryItem.");
            return false;
        }
        return true;
    }
}
