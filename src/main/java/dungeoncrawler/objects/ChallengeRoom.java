package dungeoncrawler.objects;

public class ChallengeRoom extends Room {
    private DroppedItem[] rewards;

    /**
     * Constructor for defining all Room features except Door layouts.
     * Door references must be manually defined using their respective setter methods.
     *
     * @param height        Room height, in game units
     * @param width         Room width, in game units
     * @param startX        Initial x-position of player in room, in game units
     * @param startY        Initial y-position of player in room, in game units
     * @param roomObstacles Array of all obstacle locations inside Room object
     * @param roomType      Style of the Room object
     */
    public ChallengeRoom(int height, int width, int startX, int startY, Obstacle[] roomObstacles, RoomType roomType, DroppedItem[] rewards) {
        super(height, width, startX, startY, roomObstacles, roomType);
        this.rewards = rewards;
    }

    public DroppedItem[] getRewards() {
        return rewards;
    }

    public void setRewards(DroppedItem[] rewards) {
        this.rewards = rewards;
    }
}
