package undc.items;

import org.json.JSONObject;

/**
 * Represents a Key object. Can be used to unlock/open doors that are locked.
 */
public class Key extends Item {
    @Override
    public Item copy() {
        Key key = new Key();
        copy(key);
        return key;
    }

    @Override
    public void use() {

    }

    static Key parseJSON(JSONObject o) {
        return new Key();
    }
}
