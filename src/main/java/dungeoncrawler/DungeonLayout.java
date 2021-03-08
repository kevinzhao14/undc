package dungeoncrawler;

public class DungeonLayout {
    private Room startingRoom;
    private Room exitRoom;

    public DungeonLayout(Room startingRoom, Room exitRoom) {
        this.startingRoom = startingRoom;
        this.exitRoom = exitRoom;
    }
}
