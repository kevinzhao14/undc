package undc.objects;

public class InventoryItem {
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
}
