package undc.objects;

/**
 * Class that handles doors in game. These objects grant access to rooms.
 */
public class Door extends Obstacle {

    private Room goesTo;

    public Door(double x, double y, double w, double h, Room r, DoorOrientation d) {
        super("", x, y, w, h, ObstacleType.DOOR);
        this.goesTo = r;
    }
    
    public Room getGoesTo() {
        return goesTo;
    }
}
