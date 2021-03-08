package dungeoncrawler;

public class Room {
    private int windowHeight;
    private int windowWidth;
    private Obstacle[] roomObstacles;
    private Door[] roomExits;
    private RoomType roomType;
    private String roomStyleSheetPath;

    public Room (int height, int width, Obstacle[] roomObstacles,
                 Door[] roomExits, RoomType roomType, String styleSheetPath) {
        this.height = height;
        this.width = width;
        this.roomObstacles = roomObstacles;
        this.roomExits = roomExits
        this.roomType = roomType;
        this.roomStyleSheetPath = styleSheetPath;
    }

    public Door[] getDoors() {return this.roomExits;}
    public String getStyleSheetPath() {return this.roomStyleSheetPath;}
}
