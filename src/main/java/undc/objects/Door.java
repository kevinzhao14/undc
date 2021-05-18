package undc.objects;

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
