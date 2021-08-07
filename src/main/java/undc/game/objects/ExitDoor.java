package undc.game.objects;

import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.game.calc.Direction;

/**
 * Class that handles a door used to exit the boss room.
 */
public class ExitDoor extends Door {
    public ExitDoor(Image sprite, int x, int y, int w, int h) {
        super(Direction.NORTH, x, y, w, h, null);
        setSprite(sprite);
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        String[] spriteArr = getSprite().getUrl().split("/");
        o.put("sprite", spriteArr[spriteArr.length - 1]);
        o.put("class", "ExitDoor");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        return super.parseSave(o);
    }
}
