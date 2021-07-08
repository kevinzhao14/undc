package undc.entity;

import javafx.application.Platform;
import javafx.scene.image.Image;
import org.json.JSONObject;
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
        this.posX = x;
        this.posY = y;
        this.sprite = sprite;
        this.invulnerable = true;
        this.name = name;
        this.dialogue = dialogue;
    }

    @Override
    public JSONObject saveObject() {
        return null;
    }

    @Override
    public Object parseSave(JSONObject o) {
        return null;
    }

    public String getName() {
        return name;
    }

    public Dialogue getDialogue() {
        return dialogue;
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
}
