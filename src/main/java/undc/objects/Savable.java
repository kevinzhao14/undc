package undc.objects;

import org.json.JSONObject;

public interface Savable {
    JSONObject saveObject();

    Object parseSave(JSONObject o);
}
