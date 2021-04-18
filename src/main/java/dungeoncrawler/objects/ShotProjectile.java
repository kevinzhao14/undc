package dungeoncrawler.objects;

public class ShotProjectile implements Movable {
    private Projectile projectile;

    private double posX;
    private double posY;
    private double velX;
    private double velY;
    private double height;
    private double width;
    private double distance;

    public ShotProjectile(Projectile projectile, double posX, double posY, double velX, double velY,
                          double width, double height) {
        this.projectile = projectile;
        this.posX = posX;
        this.velX = velX;
        this.velY = velY;
        this.height = height;
        this.width = width;
    }

    @Override
    public double getX() {
        return this.posX;
    }

    @Override
    public void setX(double x) {
        this.posX = x;
    }

    @Override
    public double getY() {
        return this.posY;
    }

    @Override
    public void setY(double y) {
        this.posY = y;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
