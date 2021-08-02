package undc.game.calc;

/**
 * Represents a vector.
 */
public class Vector {
    private final Coords start;
    private final Coords end;

    public Vector(Coords start, Coords end) {
        this.start = start;
        this.end = end;
    }

    public Coords getStart() {
        return start;
    }

    public Coords getEnd() {
        return end;
    }
}
