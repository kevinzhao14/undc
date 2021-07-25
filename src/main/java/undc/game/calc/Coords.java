package undc.game.calc;

/**
 * Class that handles coordinates in game, saving x and y positions.
 */
public class Coords {
    private final double x;
    private final double y;

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
