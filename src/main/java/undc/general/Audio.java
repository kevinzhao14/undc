package undc.general;

import javafx.scene.media.AudioClip;
import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.Vars;
import undc.command.DataManager;

import java.io.File;

/**
 * Class that handles playing audio in game.
 */
public class Audio {
    private String id;
    private AudioClip clip;
    private String type;

    private Audio() {

    }

    /**
     * Plays the audio associated with the passed in key in the HashMap.
     * @param id String used to locate desired AudioClip in SOUNDS HashMap
     */
    public static void playAudio(String id) {
        AudioClip clip = DataManager.SOUNDS.get(id).getClip();
        clip.play();
    }

    /**
     * Stops all AudioClips that may be playing.
     */
    public static void stopAudio() {
        for (Audio audio : DataManager.SOUNDS.values()) {
            if (audio.getClip().isPlaying()) {
                audio.getClip().stop();
            }
        }
    }

    /**
     * Turns a JSONObject into an Audio object.
     * @param o JSONObject to take data from and make an Audio object out of.
     * @return Audio object that is made from the JSONObject
     */
    public static Audio parse(JSONObject o) {
        Audio audio = new Audio();
        try {
            audio.id = o.getString("id");
        } catch (JSONException e) {
            Console.error("Invalid value for audio id.");
            return null;
        }
        try {
            audio.clip = new AudioClip(new File(o.getString("path")).toURI().toString());
        } catch (JSONException e) {
            Console.error("Invalid value for audio clip");
            return null;
        }
        try {
            audio.type = o.getString("type");
        } catch (JSONException e) {
            Console.error("Invalid value for audio type.");
            return null;
        }
        audio.clip.setVolume(Vars.d("volume"));
        return audio;
    }

    public String getId() {
        return id;
    }

    public AudioClip getClip() {
        return clip;
    }

    /**
     * Gets an AudioClip from the SOUNDS HashMap.
     * @param id String key of the value in SOUNDS
     * @return AudioClip value in SOUNDS
     */
    public static AudioClip getAudioClip(String id) {
        return DataManager.SOUNDS.get(id).getClip();
    }
}
