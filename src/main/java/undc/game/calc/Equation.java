package undc.game.calc;

/**
 * Class that handles the equation for the line used to detect collisions.
 */
public class Equation {
    private final double slope;
    private final double intercept;
    private final boolean vertical;

    /**
     * Constructor for the equation of a line, thus it has a slope and an y-intercept.
     * @param slope double that is the slope of the line
     * @param intercept double that is the line's y-intercept
     * @param vertical Whether the equation is a vertical line
     */
    public Equation(double slope, double intercept, boolean vertical) {
        this.slope = slope;
        this.intercept = intercept;
        this.vertical = vertical;
    }

    public Equation(double slope, double intercept) {
        this(slope, intercept, false);
    }

    public double getSlope() {
        return slope;
    }

    public double getIntercept() {
        return intercept;
    }

    public boolean isVertical() {
        return vertical;
    }
}
