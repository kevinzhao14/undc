package undc.objects;

public class Collision<T> {
    private Coords collisionPoint;
    private T collider;

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
