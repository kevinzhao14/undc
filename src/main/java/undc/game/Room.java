package undc.game;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.command.Console;
import undc.entity.Entity;
import undc.game.objects.Door;
import undc.game.objects.DroppedItem;
import undc.game.objects.Floor;
import undc.game.objects.Obstacle;
import undc.game.objects.ShotProjectile;
import undc.general.Savable;

import java.util.ArrayList;

/**
 * Room class implementation for handling game room obstacles and doors.
 */
public class Room implements Savable {
    private final int id;
    private final int width;
    private final int height;
    private final ArrayList<Obstacle> obstacles;
    private final ArrayList<DroppedItem> droppedItems;
    private final ArrayList<ShotProjectile> projectiles;
    private final ArrayList<Floor> floors;
    private final RoomType type;

    private ArrayList<Entity> entities;
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
        this.entities = new ArrayList<>();
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

    public boolean visited() {
        return this.visited;
    }

    public void setVisited(boolean status) {
        this.visited = status;
    }

    public void setEntities(ArrayList<Entity> entities) {
        this.entities = entities;
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

    public RoomType getType() {
        return this.type;
    }

    public ArrayList<Obstacle> getObstacles() {
        return this.obstacles;
    }

    public ArrayList<Entity> getEntities() {
        return this.entities;
    }

    public ArrayList<DroppedItem> getDroppedItems() {
        return droppedItems;
    }

    public ArrayList<ShotProjectile> getProjectiles() {
        return projectiles;
    }

    public ArrayList<Floor> getFloors() {
        return floors;
    }

    public int getId() {
        return id;
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

        JSONArray entitiesObj = new JSONArray();
        for (Entity m : entities) {
            entitiesObj.put(m.saveObject());
        }
        o.put("entities", entitiesObj);

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
    public boolean parseSave(JSONObject o) {
        try {
            visited = o.getBoolean("visited");
            JSONArray obs = o.getJSONArray("obstacles");
            for (int i = 0; i < obs.length(); i++) {
                Obstacle obstacle = Obstacle.parseSaveObject(obs.getJSONObject(i));
                if (obstacle == null) {
                    return false;
                }
                if (!obstacle.parseSave(obs.getJSONObject(i))) {
                    return false;
                }
                obstacles.add(obstacle);
            }

            // load entities
            JSONArray entitiesObj = o.getJSONArray("entities");
            for (int i = 0; i < entitiesObj.length(); i++) {
                Entity ent = Entity.parseSaveObject(entitiesObj.getJSONObject(i));
                if (ent == null) {
                    return false;
                }
                if (!ent.parseSave(entitiesObj.getJSONObject(i))) {
                    return false;
                }
                entities.add(ent);
            }

            JSONArray diObj = o.getJSONArray("droppedItems");
            for (int i = 0; i < diObj.length(); i++) {
                DroppedItem di = DroppedItem.parseSaveObject(diObj.getJSONObject(i));
                if (di == null) {
                    return false;
                }
                if (!di.parseSave(diObj.getJSONObject(i))) {
                    return false;
                }
                droppedItems.add(di);
            }

            JSONArray projObj = o.getJSONArray("projectiles");
            for (int i = 0; i < projObj.length(); i++) {
                ShotProjectile proj = ShotProjectile.parseSaveObject(projObj.getJSONObject(i));
                if (proj == null) {
                    return false;
                }
                if (!proj.parseSave(projObj.getJSONObject(i))) {
                    return false;
                }
                projectiles.add(proj);
            }
        } catch (Exception e) {
            Console.error("Failed to load Room.");
            return false;
        }
        return true;
    }

    /**
     * Loads door data for a room.
     * @param o The data to load
     * @return True on success, false on failure
     */
    public boolean parseSaveDoors(JSONObject o) {
        if (o.has("topDoor")) {
            topDoor = Door.parseSaveObject(o.getJSONObject("topDoor"));
            if (topDoor == null) {
                return false;
            }
            if (!topDoor.parseSave(o.getJSONObject("topDoor"))) {
                return false;
            }
        }
        if (o.has("bottomDoor")) {
            bottomDoor = Door.parseSaveObject(o.getJSONObject("bottomDoor"));
            if (bottomDoor == null) {
                return false;
            }
            if (!bottomDoor.parseSave(o.getJSONObject("bottomDoor"))) {
                return false;
            }
        }
        if (o.has("leftDoor")) {
            leftDoor = Door.parseSaveObject(o.getJSONObject("leftDoor"));
            if (leftDoor == null) {
                return false;
            }
            if (!leftDoor.parseSave(o.getJSONObject("leftDoor"))) {
                return false;
            }
        }
        if (o.has("rightDoor")) {
            rightDoor = Door.parseSaveObject(o.getJSONObject("rightDoor"));
            if (rightDoor == null) {
                return false;
            }
            if (!rightDoor.parseSave(o.getJSONObject("rightDoor"))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loads save data into a Room object.
     * @param o The data to load
     * @return The corresponding Room object
     */
    public static Room parseSaveObject(JSONObject o) {
        try {
            int id = o.getInt("id");
            int width = o.getInt("width");
            int height = o.getInt("height");
            RoomType type = RoomType.valueOf(o.getString("type"));
            ArrayList<Floor> floors = new ArrayList<>();
            JSONArray floorsObj = o.getJSONArray("floors");
            for (int i = 0; i < floorsObj.length(); i++) {
                Floor f = Floor.parseSaveObject(floorsObj.getJSONObject(i));
                if (f == null) {
                    return null;
                }
                floors.add(f);
            }
            return new Room(id, width, height, 0, 0, type, floors);
        } catch (Exception e) {
            Console.error("Failed to create room.");
            return null;
        }
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
}
