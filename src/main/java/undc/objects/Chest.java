package undc.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.controllers.GameController;
import undc.gamestates.GameScreen;

/**
 * Class representing a chest.
 */
public class Chest extends Obstacle implements Interactable, Savable {
    private static final Image SPRITE = new Image("textures/chest.png");

    private final Inventory contents;

    public Chest(int x, int y, Inventory contents) {
        super(SPRITE, x, y, 32, 32, ObstacleType.SOLID);
        this.contents = contents;
    }

    @Override
    public boolean interact() {
        GameScreen.getInstance().addOverlay(contents.getGraphicalInventory());
        Platform.runLater(() -> contents.getGraphicalInventory().toggle()); // show the inventory GUI
        GameController.getInstance().pause();
        return true;
    }

    public Inventory getContents() {
        return contents;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        o.put("contents", contents.saveObject());
        o.put("class", "Chest");
        return o;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return super.parseSave(o);
    }
}
