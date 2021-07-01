package undc.graphics;

import javafx.scene.image.Image;
import undc.game.calc.Direction;

/**
 * Represents a group of sprites, generally grouped by direction.
 */
public class SpriteGroup {
    public static final Image NONE = new Image("textures/none.png");

    private final Image left;
    private final Image up;
    private final Image right;
    private final Image down;

    /**
     * Constructor.
     * @param left Left sprite
     * @param up Up sprite
     * @param right Right sprite
     * @param down Down sprite
     */
    public SpriteGroup(Image left, Image up, Image right, Image down) {
        this.left = left;
        this.up = up;
        this.right = right;
        this.down = down;
    }

    /**
     * Gets the sprite based on a direction.
     * @param dir Direction of the sprite to get
     * @return Returns the corresponding sprite, the none sprite if invalid
     */
    public Image get(Direction dir) {
        if (dir == Direction.WEST) {
            return left;
        } else if (dir == Direction.NORTH) {
            return up;
        } else if (dir == Direction.EAST) {
            return right;
        } else if (dir == Direction.SOUTH) {
            return down;
        } else {
            return NONE;
        }
    }
}
