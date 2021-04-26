package dungeoncrawler.objects;

import java.util.ArrayList;

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
    private boolean visited;
    private ArrayList<Obstacle> obstacles;
    private Monster[] monsters;
    private ArrayList<DroppedItem> droppedItems;
    private ArrayList<ShotProjectile> projectiles;
    private Door topDoor;
    private Door bottomDoor;
    private Door rightDoor;
    private Door leftDoor;
    private RoomType type;
    /**
     * Constructor for defining all Room features except Door layouts.
     * Door references must be manually defined using their respective setter methods.
     *
     * @param height Room height, in game units
     * @param width Room width, in game units
     * @param startX Initial x-position of player in room, in game units
     * @param startY Initial y-position of player in room, in game units
     * @param roomType Style of the Room object
     */
    public Room(int height, int width, int startX, int startY, RoomType roomType) {
        this.startX = startX;
        this.startY = startY;
        this.height = height;
        this.width = width;
        this.obstacles = new ArrayList<>();
        this.topDoor = null;
        this.bottomDoor = null;
        this.rightDoor = null;
        this.leftDoor = null;
        this.type = roomType;
        this.droppedItems = new ArrayList<>();
        this.projectiles = new ArrayList<>();

        //Need to change this later
        this.monsters = null;
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
     * Setter method for setting Room to visited
     * @param status whether the room has been visited or not
     */
    public void setVisited(boolean status) {
        this.visited = status;
    }
    /**
     * Setter method assigning Monsters to Room
     * @param monsters array of Monsters we have to create
     */
    public void setMonsters(Monster[] monsters) {
        this.monsters = monsters;
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
    public void setStartX(int startX) {
        this.startX = startX;
    }
    /**
     * Getter method for accessing player's starting y-position, in game units
     * @return player's initial y-position in Room, in game units
     */
    public int getStartY() {
        return this.startY;
    }
    public void setStartY(int startY) {
        this.startY = startY;
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
     * Getter method for determining if Room was visited yet by player
     * @return whether Room has been visited by player or not
     */
    public boolean wasVisited() {
        return this.visited;
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
    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }
    /**
     * Getter method for accessing all Monsters in Room
     * @return array of Monsters in Room
     */
    public Monster[] getMonsters() {
        return this.monsters;
    }


    public ArrayList<DroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public ArrayList<ShotProjectile> getProjectiles() {
        return projectiles;
    }
}
