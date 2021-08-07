package undc.game.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.command.Console;
import undc.game.GameController;
import undc.general.Audio;
import undc.general.Interactable;
import undc.inventory.Inventory;
import undc.graphics.GameScreen;

/**
 * Class representing a chest.
 */
public class Chest extends Obstacle implements Interactable {
    private static final Image SPRITE = new Image("textures/obstacles/chest.png");

    private final Inventory contents;

    public Chest(int x, int y, Inventory contents) {
        super(SPRITE, x, y, 32, 32, ObstacleType.SOLID);
        this.contents = contents;
    }

    @Override
    public boolean interact() {
        Audio.playAudio("chest_open");
        GameScreen.getInstance().addOverlay(contents.getGraphicalInventory());
        Platform.runLater(() -> contents.getGraphicalInventory().toggle()); // show the inventory GUI
        GameController.getInstance().pause();
        return true;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        o.put("contents", contents.saveObject());
        o.put("class", "Chest");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        if (!super.parseSave(o)) {
            return false;
        }
        setSprite(SPRITE);
        return true;
    }

    /**
     * Loads save data into a Chest object.
     * @param o Data to load
     * @return The corresponding Chest object.
     */
    public static Chest parseSaveObject(JSONObject o) {
        try {
            Inventory contents = Inventory.parseSaveObject(o.getJSONObject("contents"));
            if (contents == null) {
                return null;
            }
            if (!contents.parseSave(o.getJSONObject("contents"))) {
                return null;
            }
            return new Chest(0, 0, contents);
        } catch (Exception e) {
            Console.error("Failed to create Chest.");
            return null;
        }
    }
}
