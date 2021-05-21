package undc.objects;

import undc.controllers.*;
import undc.gamestates.GameScreen;
import javafx.scene.image.Image;

public class Bomb extends Item {
    private double damage;
    private double radius;
    private double fuse;
    private double livefuse;

    public Bomb(String name, String spriteLocation, int stackSize,
                double damage, double radius, double fuse) {
        super(new Image(spriteLocation), name, stackSize, true);
        this.damage = damage;
        this.radius = radius;
        this.fuse = fuse;
        livefuse = -1;
    }

    public Bomb copy() {
        return new Bomb(getName(), getSprite().getUrl(), getMaxStackSize(),
                damage, radius, fuse);
    }
    public void use() {
        GameScreen screen = (GameScreen) Controller.getState();
        Room room = screen.getRoom();
        Player player = screen.getPlayer();

        //update items consumed stat for player
        player.addItemConsumed();

        //remove from inventory
        if (!player.getInventory().remove(this)) {
            Console.error("Failed to use bomb");
            return;
        }
        screen.updateHud();

        //place object as an obstacle
        double width = 20;
        double height = 20;
        double x = player.getX() + player.getWidth() / 2 - width / 2;
        double y = player.getY() + player.getHeight() / 2 - height / 2;
        ObstacleItem o = new ObstacleItem(getSprite(), x, y, width, height, ObstacleType.NONSOLID);
        Bomb timer = copy();
        o.setItem(timer);
        room.getObstacles().add(o);

        //start fuse
        timer.livefuse = fuse;
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
    public double getFuse() {
        return this.fuse;
    }

    public double getLivefuse() {
        return livefuse;
    }

    public void setLivefuse(double livefuse) {
        this.livefuse = livefuse;
    }
}
