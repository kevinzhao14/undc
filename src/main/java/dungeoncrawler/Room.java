package dungeoncrawler;

public class Room {
    private int windowHeight;
    private int windowWidth;
    private Obstacle[] roomObstacles;
    private Door topDoor;
    private Door bottomDoor;
    private Door rightDoor;
    private Door leftDoor;
    private RoomType roomType;
    private String roomStyleSheetPath;

    public Room (int height, int width, Obstacle[] roomObstacles,
                 Door topDoor, Door bottomDoor, Door rightDoor,
                 Door leftDoor, RoomType roomType, String styleSheetPath) {
        this.windowHeight = height;
        this.windowWidth = width;
        this.roomObstacles = roomObstacles;
        //this.roomExits = roomExits
        this.topDoor = topDoor;
        this.bottomDoor = bottomDoor;
        this.rightDoor = rightDoor;
        this.leftDoor = leftDoor;
        this.roomType = roomType;
        this.roomStyleSheetPath = styleSheetPath;
    }

    public int getRoomHeight() {
        return this.windowHeight;
    }
    public int getRoomWidth() {
        return this.windowWidth;
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
    public RoomType getRoomType() {
        return this.roomType;
    }
    public Obstacle[] getObstacles() {
        return this.roomObstacles;
    }
    public String getStyleSheetPath() {
        return this.roomStyleSheetPath;
    }
}
