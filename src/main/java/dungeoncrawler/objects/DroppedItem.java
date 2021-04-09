package dungeoncrawler.objects;

public class DroppedItem {
    private Item item;
    private double x;
    private double y;
    private double width;
    private double height;

    public DroppedItem(Item item, double x, double y,
                       double w, double h) {
        this.item = item;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public DroppedItem(Item item) {
        this(item, 0.0, 0.0, 0.0, 0.0);
    }

    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setWidth(double w) {
        this.width = w;
    }
    public void setHeight(double h) {
        this.height = h;
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
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
}
