package undc.objects;

/**
 * Class that handles items dropped from a player's inventory.
 */
public class DroppedItem implements Movable {
    private Item item;
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * Constructor for an item that is dropped, taking in its location, height, and width.
     * @param item Item that is dropped
     * @param x int x-position
     * @param y int y-position
     * @param w int width of the item
     * @param h int height fo the item
     */
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

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
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

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }
}
