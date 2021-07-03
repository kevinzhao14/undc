package undc.game;

import javafx.scene.image.Image;
import undc.game.calc.Direction;

/**
 * Class that handles a door used to exit the boss room.
 */
public class ExitDoor extends Door {
    public ExitDoor(Image sprite, int x, int y, int w, int h) {
        super(Direction.NORTH, x, y, w, h, null);
        setSprite(sprite);
    }
}
