package undc.objects;

/**
 * Represents a cardinal direction.
 */
public enum Direction {
    NONE, NORTH, SOUTH, WEST, EAST;

    /**
     * Parses a string into a Direction.
     * @param str String to parse
     * @return Returns the corresponding direction, None if improper string.
     */
    public static Direction parse(String str) {
        str = str.toLowerCase();
        switch (str) {
            case "left":
                return WEST;
            case "up":
                return NORTH;
            case "right":
                return EAST;
            case "down":
                return SOUTH;
            default:
                return NONE;
        }
    }
}
