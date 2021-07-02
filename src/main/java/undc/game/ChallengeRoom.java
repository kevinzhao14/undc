package undc.game;

import undc.general.Audio;
import undc.inventory.Inventory;

import java.util.ArrayList;

/**
 * Class that handles the construction of a room of heightened difficulty called a ChallengeRoom.
 */
public class ChallengeRoom extends Room {
    private boolean completed;
    private final Inventory rewards;

    /**
     * Constructor for defining all Room features except Door layouts.
     * Door references must be manually defined using their respective setter methods.
     *
     * @param height Room height, in game units
     * @param width Room width, in game units
     * @param startX Initial x-position of player in room, in game units
     * @param startY Initial y-position of player in room, in game units
     * @param rewards Rewards for beating the room
     */
    public ChallengeRoom(int width, int height, int startX, int startY, Inventory rewards, ArrayList<Floor> floors) {
        super(width, height, startX, startY, RoomType.CHALLENGEROOM, floors);
        this.rewards = rewards;
        this.completed = false;
    }

    public Inventory getRewards() {
        return rewards;
    }

    public boolean isCompleted() {
        return completed;
    }

    /**
     * Updates completed status of challenge room and stops music.
     * @param completed Whether the room has been completed.
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
        if (completed && Audio.getSoundsClip("challenge_room").isPlaying()) {
            Audio.getSoundsClip("challenge_room").stop();
        }
    }
}
