package dungeoncrawler.objects;

public class Door extends Obstacle {

    private DoorOrientation orientation;
    private Room goesTo;

    public Door(int x, int y, int w, int h, Room r, DoorOrientation d) {
        super(x, y, w, h, ObstacleType.DOOR);
        this.goesTo = r;
        this.orientation = d;

    }
    
    public DoorOrientation getOrientation() {
        return orientation;
    }

    public Room getGoesTo() {
        return goesTo;
    }

    public void setOrientation(DoorOrientation d) {
        this.orientation = d;
    }

    public void setGoesTo(Room r) {
        this.goesTo = r;
    }

}
