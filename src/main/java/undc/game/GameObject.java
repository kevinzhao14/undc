package undc.game;

/**
 * Interface containing getters and setters for movement in game.
 */
public abstract class GameObject {
    protected double x;
    protected double y;
    protected double z;
    protected int width;
    protected int height;

    /**
     * Constructor.
     */
    protected GameObject() {
        x = 0;
        y = 0;
        z = 0;
        width = 0;
        height = 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
