package undc.objects;

public class Dummy implements Movable {
    private double x;
    private double y;
    private double width;
    private double height;

    public Dummy(double x, double y, double w, double h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public double getWidth() {
        return width;
    }
}
