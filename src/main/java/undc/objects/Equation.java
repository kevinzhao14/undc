package undc.objects;

public class Equation {
    private double slope;
    private double intercept;
    private boolean vertical;

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
