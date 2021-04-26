package dungeoncrawler.objects;

public class Door extends Obstacle {

    private DoorOrientation orientation;
    private Room goesTo;

    public Door(double x, double y, double w, double h, Room r, DoorOrientation d) {
        super("", x, y, w, h, ObstacleType.DOOR);
        this.goesTo = r;
        this.orientation = d;

    }
    
    public DoorOrientation getOrientation() {
        return orientation;
    }

    public Room getGoesTo() {
        return goesTo;
    }
}
