package undc.game;

import org.json.JSONObject;
import undc.general.Audio;
import undc.inventory.Inventory;

import java.util.ArrayList;

/**
 * Class that handles the construction of a room of heightened difficulty called a ChallengeRoom.
 */
public class ChallengeRoom extends Room {
    private final Inventory rewards;

    private boolean completed;

    /**
     * Constructor for defining all Room features except Door layouts.
     * Door references must be manually defined using their respective setter methods.
     *
     * @param height Room height, in game units
     * @param width Room width, in game units
     * @param x Initial x-position of player in room, in game units
     * @param y Initial y-position of player in room, in game units
     * @param rewards Rewards for beating the room
     */
    public ChallengeRoom(int id, int width, int height, int x, int y, Inventory rewards, ArrayList<Floor> floors) {
        super(id, width, height, x, y, RoomType.CHALLENGEROOM, floors);
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
        if (completed && Audio.getAudioClip("challenge_room").isPlaying()) {
            Audio.getAudioClip("challenge_room").stop();
        }
    }

    /**
     * Changes the door sprites to be unblocked.
     */
    public void openDoors() {
        for (Door d : getDoors()) {
            d.setSprite(LayoutGenerator.DOORS.get(d.getOrientation()));
        }
    }

    /**
     * Changes the door sprites to be blocked.
     */
    public void closeDoors() {
        for (Door d : getDoors()) {
            d.setSprite(LayoutGenerator.DOORS_BLOCKED.get(d.getOrientation()));
        }
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        o.put("completed", completed);
        o.put("rewards", rewards.saveObject());
        o.put("class", "ChallengeRoom");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        return super.parseSave(o);
    }
}
