package undc.game;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.command.Console;
import undc.general.Savable;


/**
 * Class implementation of the DungeonLayout class. DungeonLayout will
 * be used for traversing and maintaining the graph/layout of Rooms
 * which compose the game map.
 */
public class DungeonLayout implements Savable {
    private Room startingRoom;
    private Room exitRoom;
    private Room[][] grid;

    /**
     * Constructor for initializing starting and ending Rooms in Dungeon map.
     * @param startingRoom Starting Room of current game instance
     * @param exitRoom Final Room of current game instance
     * @param grid Grid of all the rooms in current game instance
     */
    public DungeonLayout(Room startingRoom, Room exitRoom, Room[][] grid) {
        this.startingRoom = startingRoom;
        this.exitRoom = exitRoom;
        this.grid = grid;
    }

    /**
     * Getter method for accessing starting Room of Dungeon map.
     * @return the starting Room of the Dungeon map
     */
    public Room getStartingRoom() {
        return this.startingRoom;
    }

    /**
     * Getter method of accessing final/exit Room of Dungeon map.
     * @return the final/exit Room of the Dungeon map
     */
    public Room getExitRoom() {
        return this.exitRoom;
    }

    /**
     * Getter method of accessing the grid of rooms of the Dungeon map.
     * @return the grid of rooms of the Dungeon map
     */
    public Room[][] getGrid() {
        return this.grid;
    }

    /**
     * Gets a room by its id.
     * @param id Id of the room to get
     * @return The corresponding room
     */
    public Room get(int id) {
        for (Room[] row : grid) {
            for (Room r : row) {
                if (r != null && r.getId() == id) {
                    return r;
                }
            }
        }
        return null;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("start", startingRoom.getId());
        o.put("exit", exitRoom.getId());
        JSONArray gridObj = new JSONArray();
        for (Room[] row : grid) {
            JSONArray rowObj = new JSONArray();
            for (Room r : row) {
                rowObj.put(r == null ? "" : r.saveObject());
            }
            gridObj.put(rowObj);
        }
        o.put("grid", gridObj);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            JSONArray gridObj = o.getJSONArray("grid");
            if (gridObj.length() == 0) {
                grid = new Room[0][0];
            } else {
                grid = new Room[gridObj.length()][gridObj.getJSONArray(0).length()];
                for (int i = 0; i < gridObj.length(); i++) {
                    JSONArray row = gridObj.getJSONArray(i);
                    for (int j = 0; j < row.length(); j++) {
                        if (row.get(j) instanceof String) {
                            grid[i][j] = null;
                        } else {
                            Room r = Room.parseSaveObject(row.getJSONObject(j));
                            if (r == null) {
                                return false;
                            }
                            if (!r.parseSave(row.getJSONObject(j))) {
                                return false;
                            }
                            grid[i][j] = r;
                        }
                    }
                }
                // load doors after loading all rooms since doors depend on having the rooms
                for (int i = 0; i < gridObj.length(); i++) {
                    JSONArray row = gridObj.getJSONArray(i);
                    for (int j = 0; j < row.length(); j++) {
                        if (!grid[i][j].parseSaveDoors(row.getJSONObject(j))) {
                            return false;
                        }
                    }
                }
            }
            startingRoom = get(o.getInt("start"));
            exitRoom = get(o.getInt("exit"));
        } catch (Exception e) {
            Console.error("Failed to load Dungeon Layout.");
            return false;
        }
        return true;
    }
}
