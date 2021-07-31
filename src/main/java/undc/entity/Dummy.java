package undc.entity;

import undc.game.GameObject;

/**
 * Represents a dummy entity/movable object used in imiatating an object's position.
 */
public class Dummy extends GameObject {
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
}
