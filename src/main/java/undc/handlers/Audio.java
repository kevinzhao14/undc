package undc.handlers;

import javafx.scene.media.AudioClip;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;
import undc.controllers.DataManager;

import java.io.File;

/**
 * Class that handles playing audio in game.
 */
public class Audio {
    private String id;
    private AudioClip clip;

    private Audio() {

    }

    /**
     * Plays the audio associated with the passed in key in the HashMap.
     * @param id String used to locate desired AudioClip in SOUNDS HashMap
     */
    public static void playAudio(String id) {
        AudioClip clip = DataManager.SOUNDS.get(id);
        clip.setVolume(Vars.d("volume"));
        clip.play();
    }

    /**
     * Stops all AudioClips that may be playing.
     */
    public static void stopAudio() {
        for (AudioClip clip : DataManager.SOUNDS.values()) {
            if (clip.isPlaying()) {
                clip.stop();
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
     * @param sound String key of the value in SOUNDS
     * @return AudioClip value in SOUNDS
     */
    public static AudioClip getSoundsClip(String sound) {
        return DataManager.SOUNDS.get(sound);
    }
}