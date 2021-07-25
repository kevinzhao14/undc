package undc.entity;

import undc.general.Movable;

/**
 * Represents a dummy entity/movable object used in imiatating an object's position.
 */
public class Dummy implements Movable {
    private final int width;
    private final int height;

    private double x;
    private double y;

    /**
     * Creates a Dummy object.
     * @param x X position
     * @param y Y position
     * @param w Width
     * @param h Height
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
