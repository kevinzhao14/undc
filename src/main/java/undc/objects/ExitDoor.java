package undc.objects;

/**
 * Class that handles a door used to exit the boss room.
 */
public class ExitDoor extends Door {
    public ExitDoor(int x, int y, int w, int h) {
        super(x, y, w, h, null, DoorOrientation.TOP);
    }
}
