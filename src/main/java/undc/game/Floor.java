package undc.game;

import javafx.scene.image.Image;
import undc.general.Movable;

/**
 * Represents a floor tile.
 */
public class Floor implements Movable {
    private final Image sprite;
    private final double x;
    private final double y;
    private final int width;
    private final int height;

    /**
     * Constructor.
     * @param sprite Sprite of the floor
     * @param width Width of the floor
     * @param height Height of the floor
     * @param x X position of the floor
     * @param y Y position of the floor
     */
    public Floor(Image sprite, int width, int height, double x, double y) {
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {

    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {

    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    public Image getSprite() {
        return sprite;
    }
}
