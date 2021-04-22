package dungeoncrawler.objects;

public class ChallengeRoom extends Room {
    private Inventory rewards;

    /**
     * Constructor for defining all Room features except Door layouts.
     * Door references must be manually defined using their respective setter methods.
     *
     * @param height        Room height, in game units
     * @param width         Room width, in game units
     * @param startX        Initial x-position of player in room, in game units
     * @param startY        Initial y-position of player in room, in game units
     * @param roomObstacles Array of all obstacle locations inside Room object
     * @param rewards Rewards for beating the room
     */
    public ChallengeRoom(int height, int width, int startX, int startY, Obstacle[] roomObstacles, Inventory rewards) {
        super(height, width, startX, startY, roomObstacles, RoomType.CHALLENGEROOM);
        this.rewards = rewards;
    }

    public Inventory getRewards() {
        return rewards;
    }

    public void setRewards(Inventory rewards) {
        this.rewards = rewards;
    }
}
