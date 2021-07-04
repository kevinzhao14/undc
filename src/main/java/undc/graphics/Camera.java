package undc.graphics;

/**
 * Represents a camera. Used to set the visible viewport.
 */
public class Camera {
    private double x;
    private double y;

    public Camera() {
        x = 0;
        y = 0;
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
}
