package dungeoncrawler.objects;

import javafx.scene.image.Image;

public class Bomb extends Item {
    private double damage;
    private double radius;
    private double fuseRoom;

    public Bomb(String name, String spriteLocation, int stackSize,
                double damage, double radius, double fuseRoom) {
        super(new Image(spriteLocation), name, stackSize, true);
        this.damage = damage;
        this.radius = radius;
        this.fuseRoom = fuseRoom;
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
        return this.fuseRoom;
    }
    public void setFuseRoom(double fuseRoom) {
        this.fuseRoom = fuseRoom;
    }
}
