package undc.inventory;

import org.json.JSONObject;
import undc.items.Item;
import undc.general.Savable;

/**
 * Class that handles Items that have quantities.
 */
public class InventoryItem implements Savable {
    private Item item;
    private int quantity;
    private boolean infinite;

    public InventoryItem(Item i, int q) {
        this.item = i;
        this.quantity = q;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
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
        o.put("item", item.getId());
        o.put("quantity", quantity);
        o.put("infinite", infinite);
        return o;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }
}
