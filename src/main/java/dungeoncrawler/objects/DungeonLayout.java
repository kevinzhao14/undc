package dungeoncrawler.objects;

/**
 * Class implementation of the DungeonLayout class. DungeonLayout will
 * be used for traversing and maintaining the graph/layout of Rooms
 * which compose the game map.
 *
 * @author Manas Harbola
 * @version 1.0
 */
public class DungeonLayout {
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

}
