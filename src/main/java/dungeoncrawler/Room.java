package dungeoncrawler;

public class Room {
    private int height;
    private int width;
    private int startX;
    private int startY;
    private Obstacle[] obstacles;
    private Door topDoor;
    private Door bottomDoor;
    private Door rightDoor;
    private Door leftDoor;
    private RoomType type;

    public Room (int height, int width, Obstacle[] roomObstacles,
                 Door topDoor, Door bottomDoor, Door rightDoor,
                 Door leftDoor, RoomType roomType) {
        this.height = height;
        this.width = width;
        this.obstacles = roomObstacles;
        this.topDoor = topDoor;
        this.bottomDoor = bottomDoor;
        this.rightDoor = rightDoor;
        this.leftDoor = leftDoor;
        this.type = roomType;
    }

    public Room (int height, int width, Obstacle[] roomObstacles, RoomType roomType) {
        this(height, width, roomObstacles, null, null, null, null, roomType);
    }

    public void setTopDoor(Door d) {
        this.topDoor = d;
    }
    public void setBottomDoor(Door d) {
        this.bottomDoor = d;
    }
    public void setRightDoor(Door d) {
        this.rightDoor = d;
    }
    public void setLeftDoor(Door d) {
        this.leftDoor = d;
    }

    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }
    public int getStartX() {
        return this.startX;
    }
    public int getStartY() {
        return this.startY;
    }
    public Door getTopDoor() {
        return this.topDoor;
    }
    public Door getBottomDoor() {
        return this.bottomDoor;
    }
    public Door getRightDoor() {
        return this.rightDoor;
    }
    public Door getLeftDoor() {
        return this.leftDoor;
    }
    public RoomType getType() {
        return this.type;
    }
    public Obstacle[] getObstacles() {
        return this.obstacles;
    }
}
