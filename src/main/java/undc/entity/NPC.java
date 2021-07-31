package undc.entity;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.command.Console;
import undc.general.Interactable;
import undc.graphics.GameScreen;

/**
 * Represents an NPC, a non-player character.
 */
public class NPC extends Entity implements Interactable {
    private final String name;
    private final Dialogue dialogue;

    /**
     * Constructor.
     * @param width Width of the NPC
     * @param height Height of the NPC
     * @param x X position of the NPC
     * @param y Y position of the NPC
     * @param sprite Sprite of the NPC
     * @param name Name of the NPC
     * @param dialogue Dialogue options of the NPC
     */
    public NPC(int width, int height, double x, double y, Image sprite, String name, Dialogue dialogue) {
        super(100, 0, width, height, null);
        this.x = x;
        this.y = y;
        this.sprite = sprite;
        this.invulnerable = true;
        this.name = name;
        this.dialogue = dialogue;
    }

    private NPC(String name, Dialogue dialogue) {
        this.name = name;
        this.dialogue = dialogue;
    }

    @Override
    public boolean interact() {
        String line = dialogue.next();
        if (!line.equals("")) {
            Platform.runLater(() -> GameScreen.getInstance().getHud().setDialogue(name + ": " + line));
        } else {
            Platform.runLater(() -> GameScreen.getInstance().getHud().hideDialogue());
        }
        return true;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("width", width);
        o.put("height", height);
        o.put("posX", x);
        o.put("posY", y);
        String[] spriteArr = sprite.getUrl().split("/");
        o.put("sprite", spriteArr[spriteArr.length - 1]);
        o.put("name", name);
        o.put("dialogue", dialogue.saveObject());
        o.put("class", "NPC");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            maxHealth = 100;
            health = 100;
            width = o.getInt("width");
            height = o.getInt("height");
            x = o.getDouble("posX");
            y = o.getDouble("posY");
            setSprite(new Image("entities/npcs/" + o.getString("sprite")));
        } catch (Exception e) {
            Console.error("Failed to load NPC.");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into an NPC object.
     * @param o The data to load
     * @return The corresponding NPC object
     */
    public static NPC parseSaveObject(JSONObject o) {
        try {
            String name = o.getString("name");
            Dialogue dialogue = Dialogue.parseSaveObject(o.getJSONObject("dialogue"));
            if (dialogue == null) {
                return null;
            }
            if (!dialogue.parseSave(o.getJSONObject("dialogue"))) {
                return null;
            }
            return new NPC(name, dialogue);
        } catch (Exception e) {
            Console.error("Failed to create NPC");
            return null;
        }
    }
}
