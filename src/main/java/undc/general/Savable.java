package undc.general;

import org.json.JSONObject;

/**
 * Represents a class/object whose data can and should be saved.
 */
public interface Savable {
    /**
     * Creates a JSONObject with the object's data.
     * @return Returns the corresponding JSONObject
     */
    JSONObject saveObject();

    /**
     * Loads the save data into the object.
     * @param o The data to load
     * @return True on success, false on failure
     */
    boolean parseSave(JSONObject o);
}
