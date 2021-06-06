package undc.objects;
import javafx.scene.image.Image;
import org.json.JSONObject;

public class Key extends Item {
    private Key(String name, String path, boolean isDroppable) {
        //note: keys are not stackable
        super(11, new Image(path), name, 0, isDroppable);
    }

    private Key() {

    }

    public Item copy() {
        return new Key(getName(), getSprite().getUrl(), isDroppable());
    }

    //does nothing
    public void use() {
        return;
    }

    static Key parseJSON(JSONObject o) {
        Key key = new Key();
        return key;
    }
}
