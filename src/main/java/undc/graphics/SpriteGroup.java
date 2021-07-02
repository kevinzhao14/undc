package undc.graphics;

import javafx.scene.image.Image;
import undc.game.calc.Direction;

import java.util.HashMap;

/**
 * Represents a group of sprites, generally grouped by direction.
 */
public class SpriteGroup {
    public static final Image NONE = new Image("textures/none.png");

    private final HashMap<Direction, Image> sprites = new HashMap<>();

    /**
     * Constructor for four directions.
     * @param west Left sprite
     * @param north Up sprite
     * @param east Right sprite
     * @param south Down sprite
     */
    public SpriteGroup(Image west, Image north, Image east, Image south) {
        this(west, null, north, null, east, null, south, null);
    }

    /**
     * Constructor for eight directions.
     * @param west West sprite
     * @param northwest Northwest sprite
     * @param north North sprite
     * @param northeast Northeast sprite
     * @param east East sprite
     * @param southeast Southeast sprite
     * @param south South sprite
     * @param southwest Southwest sprite
     */
    public SpriteGroup(Image west, Image northwest, Image north, Image northeast, Image east, Image southeast,
                       Image south, Image southwest) {
        if (west != null) {
            sprites.put(Direction.WEST, west);
        }
        if (northwest != null) {
            sprites.put(Direction.NORTHWEST, northwest);
        }
        if (north != null) {
            sprites.put(Direction.NORTH, north);
        }
        if (northeast != null) {
            sprites.put(Direction.NORTHEAST, northeast);
        }
        if (east != null) {
            sprites.put(Direction.EAST, east);
        }
        if (southeast != null) {
            sprites.put(Direction.SOUTHEAST, southeast);
        }
        if (south != null) {
            sprites.put(Direction.SOUTH, south);
        }
        if (southwest != null) {
            sprites.put(Direction.SOUTHWEST, southwest);
        }
    }

    /**
     * Gets the sprite based on a direction.
     * @param dir Direction of the sprite to get
     * @return Returns the corresponding sprite, the none sprite if invalid
     */
    public Image get(Direction dir) {
        Image sprite = sprites.get(dir);
        return sprite == null ? NONE : sprite;
    }
}
