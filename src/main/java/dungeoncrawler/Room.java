package dungeoncrawler;

/**
 * Room class implementation for handling game room
 * obstacles and doors.
 *
 * @version 1.0
 * @author Manas Harbola
 */
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

    /**
     * Constructor for defining important Room features and door layouts.
     *
     * @param height Room height, in game units
     * @param width Room width, in game units
     * @param startX Initial x-position of player in room, in game units
     * @param startY Initial y-position of player in room, in game units
     * @param roomObstacles Array of all obstacle locations inside Room object
     * @param topDoor Reference to Door object located at top wall of Room
     * @param bottomDoor Reference to Door object located at bottom wall of Room
     * @param rightDoor Reference to Door object located at right wall of Room
     * @param leftDoor Reference to Door object located at left wall of Room
     * @param roomType Style of the Room object
     */
    public Room(int height, int width, int startX, int startY, Obstacle[] roomObstacles,
                 Door topDoor, Door bottomDoor, Door rightDoor,
                 Door leftDoor, RoomType roomType) {
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.width = width;
        this.obstacles = roomObstacles;
        this.topDoor = topDoor;
        this.bottomDoor = bottomDoor;
        this.rightDoor = rightDoor;
        this.leftDoor = leftDoor;
        this.type = roomType;
    }

    /**
     * Secondary constructor for defining all Room features except Door layouts.
     *
     * @param height Room height, in game units
     * @param width Room width, in game units
     * @param startX Initial x-position of player in room, in game units
     * @param startY Initial y-position of player in room, in game units
     * @param roomObstacles Array of all obstacle locations inside Room object
     * @param roomType Style of the Room object
     */
    public Room(int height, int width, int startX, int startY, Obstacle[] roomObstacles, RoomType roomType) {
        this(height, width, startX, startY, roomObstacles, null, null, null, null, roomType);
    }
    /**
     * Setter method for top Door in Room.
     *
     * @param d Reference to Door object located at top wall of Room
     */
    public void setTopDoor(Door d) {
        this.topDoor = d;
    }
    /**
     * Setter method for bottom Door in Room.
     *
     * @param d Reference to Door object located at bottom wall of Room
     */
    public void setBottomDoor(Door d) {
        this.bottomDoor = d;
    }
    /**
     * Setter method for right Door in Room.
     *
     * @param d Reference to Door object located at right wall of Room
     */
    public void setRightDoor(Door d) {
        this.rightDoor = d;
    }
    /**
     * Setter method for left Door in Room.
     *
     * @param d Reference to Door object located at left wall of Room
     */
    public void setLeftDoor(Door d) {
        this.leftDoor = d;
    }
    /**
     * Getter method for accessing height of Room, in game units
     * @return Room height, in game units
     */
    public int getHeight() {
        return this.height;
    }
    /**
     * Getter method for accessing width of Room, in game units
     * @return Room width, in game units
     */
    public int getWidth() {
        return this.width;
    }
    /**
     * Getter method for accessing player's starting x-position, in game units
     * @return player's initial x-position in Room, in game units
     */
    public int getStartX() {
        return this.startX;
    }
    /**
     * Getter method for accessing player's starting y-position, in game units
     * @return player's initial y-position in Room, in game units
     */
    public int getStartY() {
        return this.startY;
    }
    /**
     * Getter method for accessing top Door object in Room
     * @return Door object located at top wall of Room
     */
    public Door getTopDoor() {
        return this.topDoor;
    }
    /**
     * Getter method for accessing bottom Door object in Room
     * @return Door object located at bottom wall of Room
     */
    public Door getBottomDoor() {
        return this.bottomDoor;
    }
    /**
     * Getter method for accessing right Door object in Room
     * @return Door object located at right wall of Room
     */
    public Door getRightDoor() {
        return this.rightDoor;
    }
    /**
     * Getter method for accessing left Door object in Room
     * @return Door object located at left wall of Room
     */
    public Door getLeftDoor() {
        return this.leftDoor;
    }
    /**
     * Getter method for accessing RoomType enum of Room
     * @return RoomType enum of Room
     */
    public RoomType getType() {
        return this.type;
    }
    /**
     * Getter method for accessing all Obstacle objects in Room
     * @return array of Obstacles in Room
     */
    public Obstacle[] getObstacles() {
        return this.obstacles;
    }
}
