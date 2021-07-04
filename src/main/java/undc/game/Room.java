package undc.game;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.entity.Monster;
import undc.general.Savable;

import java.util.ArrayList;

/**
 * Room class implementation for handling game room obstacles and doors.
 */
public class Room implements Savable {
    private final int id;
    private final int height;
    private final int width;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<DroppedItem> droppedItems;
    private final ArrayList<ShotProjectile> projectiles;
    private final ArrayList<Floor> floors;
    private final RoomType type;

    private ArrayList<Monster> monsters;
    private int startX;
    private int startY;
    private boolean visited;
    private Door topDoor;
    private Door bottomDoor;
    private Door rightDoor;
    private Door leftDoor;

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
    public Room(int id, int width, int height, int startX, int startY, RoomType roomType, ArrayList<Floor> floors) {
        this.id = id;
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
        this.floors = floors;

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

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("width", width);
        o.put("height", height);
        o.put("visited", visited);
        o.put("type", type.toString());

        JSONArray obstaclesObj = new JSONArray();
        for (Obstacle obs : obstacles) {
            obstaclesObj.put(obs.saveObject());
        }
        o.put("obstacles", obstaclesObj);

        JSONArray monstersObj = new JSONArray();
        for (Monster m : monsters) {
            monstersObj.put(m.saveObject());
        }
        o.put("monsters", monstersObj);

        JSONArray droppedItemsObj = new JSONArray();
        for (DroppedItem d : droppedItems) {
            droppedItemsObj.put(d.saveObject());
        }
        o.put("droppedItems", droppedItemsObj);

        JSONArray projectilesObj = new JSONArray();
        for (ShotProjectile p : projectiles) {
            projectilesObj.put(p.saveObject());
        }
        o.put("projectiles", projectilesObj);

        JSONArray floorsObj = new JSONArray();
        for (Floor f : floors) {
            floorsObj.put(f.saveObject());
        }
        o.put("floors", floorsObj);

        if (topDoor != null) {
            o.put("topDoor", topDoor.saveObject());
        }
        if (bottomDoor != null) {
            o.put("bottomDoor", bottomDoor.saveObject());
        }
        if (leftDoor != null) {
            o.put("leftDoor", leftDoor.saveObject());
        }
        if (rightDoor != null) {
            o.put("rightDoor", rightDoor.saveObject());
        }

        return o;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    /**
     * Gets a list of all the doors in the room.
     * @return Returns all the doors
     */
    public ArrayList<Door> getDoors() {
        ArrayList<Door> doors = new ArrayList<>();
        if (topDoor != null) {
            doors.add(topDoor);
        }
        if (leftDoor != null) {
            doors.add(leftDoor);
        }
        if (bottomDoor != null) {
            doors.add(bottomDoor);
        }
        if (rightDoor != null) {
            doors.add(rightDoor);
        }
        return doors;
    }

    public int getId() {
        return id;
    }
}
