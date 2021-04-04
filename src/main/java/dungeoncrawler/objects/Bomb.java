package dungeoncrawler.objects;

public class Bomb extends Item {
    double damage;
    double radius;
    double fuse;

    public Bomb(double damage, double radius, double fuse) {
        //dummy path for now
        super("Bomb path", "Bomb");
        this.damage = damage;
        this.radius = radius;
        this.fuse = fuse;
    }

    public double getDamage() {
        return this.damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }
    public double getRadius() {
        return this.radius;
    }
    public void setRadius(double radius) {
        this.radius = radius;
    }
    public double getFuseRoom() {
        return this.fuse;
    }
    public void setFuseRoom(double fuse) {
        this.fuse = fuse;
    }
}
