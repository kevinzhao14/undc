package undc.game.objects;

import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.command.Console;
import undc.game.LayoutGenerator;
import undc.game.Room;
import undc.game.calc.Direction;
import undc.graphics.GameScreen;

/**
 * Class that handles doors in game. These objects grant access to rooms.
 */
public class Door extends Obstacle {
    private final Room goesTo;
    private final Direction orientation;

    /**
     * Constructor for a door.
     * @param orientation Orientation of the door
     * @param x X position of the door
     * @param y Y position of the door
     * @param w Width of the door
     * @param h Height of the door
     * @param r Room the door goes to
     */
    public Door(Direction orientation, double x, double y, int w, int h, Room r) {
        super(LayoutGenerator.DOORS.get(orientation), x, y, w, h, ObstacleType.DOOR);
        this.goesTo = r;
        this.orientation = orientation;
    }
    
    public Room getGoesTo() {
        return goesTo;
    }

    public Direction getOrientation() {
        return orientation;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = super.saveObject();
        o.put("goesTo", goesTo == null ? "" : goesTo.getId());
        o.put("orientation", orientation.toString());
        o.put("class", "Door");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        if (!super.parseSave(o)) {
            return false;
        }
        setSprite(LayoutGenerator.DOORS.get(orientation));
        setType(ObstacleType.DOOR);
        return true;
    }

    /**
     * Loads save data into a Door object.
     * @param o The data to load
     * @return The corresponding Door object
     */
    public static Door parseSaveObject(JSONObject o) {
        try {
            String c = o.getString("class");
            Direction orientation = Direction.valueOf(o.getString("direction"));
            Room goesTo = GameScreen.getInstance().getLayout().get(o.getInt("goesTo"));
            if (c.equals("Door")) {
                return new Door(orientation, 0, 0, 0, 0, goesTo);
            } else if (c.equals("ExitDoor")) {
                Image sprite = new Image("textures/room/doors/" + o.getString("sprite"));
                return new ExitDoor(sprite, 0, 0, 0, 0);
            } else {
                Console.error("Invalid door type.");
                return null;
            }
        } catch (Exception e) {
            Console.error("Failed to create Door.");
            return null;
        }
    }
}
