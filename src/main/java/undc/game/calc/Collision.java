package undc.game.calc;

/**
 * Class that handles the collision of objects in game, storing their position and the objects that collided.
 */
public class Collision<T> {
    private final Coords collisionPoint;
    private final T collider;

    public Collision(Coords collisionPoint, T collider) {
        this.collisionPoint = collisionPoint;
        this.collider = collider;
    }

    public Collision() {
        this(null, null);
    }

    public Coords getCollisionPoint() {
        return collisionPoint;
    }

    public T getCollider() {
        return collider;
    }
}
