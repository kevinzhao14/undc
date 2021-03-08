package dungeoncrawler;

public class Door extends Obstacle {
    //A Door connects two Rooms
    private Room room1;
    private Room room2;

    public Door(int windowHeight, int windowWidth, int xPos,
                int yPos, Room room1, Room room2) {
        super(windowHeight, windowWidth, xPos, yPos);
        this.room1 = room1;
        this.room2 = room2;
    }

    public Room getRoom1() {
        return this.room1;
    }
    public Room getRoom2() {
        return this.room2;
    }
}
