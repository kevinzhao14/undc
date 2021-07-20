package undc.general;

import org.json.JSONObject;

/**
 * Represents a class/object whose data can and should be saved.
 */
public interface Savable {
    JSONObject saveObject();

    boolean parseSave(JSONObject o);
}
