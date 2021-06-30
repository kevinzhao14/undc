package undc.objects;

/**
 * Class that handles storing information for a Movable object.
 */
public class Dummy implements Movable {
    private double x;
    private double y;
    private final int width;
    private final int height;

    /**
     * Creates a Dummy object.
     * @param x double x-cord
     * @param y double y-cord
     * @param w int width
     * @param h int height
     */
    public Dummy(double x, double y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }
}
