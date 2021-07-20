package undc.game;

import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.items.Item;

/**
 * CLass that handles items that can act as physical obstacles within the game (ex: a bomb).
 */
public class ObstacleItem extends Obstacle {
    private Item item;

    public ObstacleItem(Image sprite, double x, double y, int w, int h, ObstacleType type) {
        super(sprite, x, y, w, h, type);
    }

    private ObstacleItem() {

    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        o.put("item", item.getId());
        o.put("class", "ObstacleItem");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        if (!super.parseSave(o)) {
            return false;
        }
        try {
            item = DataManager.ITEMS.get(o.getString("item"));
            setSprite(item.getSprite());
        } catch (Exception e) {
            Console.error("Failed to load ObstacleItem.");
            return false;
        }
        return true;
    }

    public static ObstacleItem parseSaveObject(JSONObject o) {
        return new ObstacleItem();
    }
}
