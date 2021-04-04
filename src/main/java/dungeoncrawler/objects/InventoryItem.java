package dungeoncrawler.objects;

public class InventoryItem {

    private Item item;
    private int quantity;

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
}
