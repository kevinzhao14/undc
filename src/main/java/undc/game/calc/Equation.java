package undc.game.calc;

/**
 * Class that handles the equation for the line used to detect collisions.
 */
public class Equation {
    private double slope;
    private double intercept;
    private boolean vertical;

    /**
     * Constructor for the equation of a line, thus it has a slope and an y-intercept.
     * @param slope double that is the slope of the line
     * @param intercept double that is the line's y-intercept
     */
    public Equation(double slope, double intercept) {
        this.slope = slope;
        this.intercept = intercept;
        vertical = false;
    }

    public void setVertical(double x) {
        vertical = true;
        intercept = x;
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
