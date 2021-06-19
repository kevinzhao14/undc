package undc.objects;

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
    private ArrayList<Monster> monsters;
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
    public Room(int width, int height, int startX, int startY, RoomType roomType) {
        this.startX = startX;
        this.startY = startY;
        this.width = width;
        this.height = height;
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

    public void setVisited(boolean status) {
        this.visited = status;
    }

    public void setMonsters(ArrayList<Monster> monsters) {
        this.monsters = monsters;
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

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return this.startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
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

    public boolean wasVisited() {
        return this.visited;
    }

    public RoomType getType() {
        return this.type;
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public ArrayList<Monster> getMonsters() {
        return this.monsters;
    }

    public ArrayList<DroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public ArrayList<ShotProjectile> getProjectiles() {
        return projectiles;
    }
}
