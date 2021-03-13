package dungeoncrawler;

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
    /**
     * Constructor for initializing starting and ending Rooms in Dungeon map.
     * @param startingRoom Starting Room of current game instance
     * @param exitRoom Final Room of current game instance
     */
    public DungeonLayout(Room startingRoom, Room exitRoom) {
        this.startingRoom = startingRoom;
        this.exitRoom = exitRoom;
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
}
