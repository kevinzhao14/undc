package undc.game;

import undc.game.calc.Direction;

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
}
