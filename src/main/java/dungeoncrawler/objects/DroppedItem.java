package dungeoncrawler.objects;

public class DroppedItem {
    private Item item;
    private double x;
    private double y;
    private double w;
    private double h;

    public DroppedItem(Item item, double x, double y,
                       double w, double h) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public DroppedItem(Item item) {
        this(item, 0.0, 0.0, 0.0, 0.0);
    }

    public Item getItem() {
        return item;
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getW() {
        return w;
    }
    public double getH() {
        return h;
    }
}
