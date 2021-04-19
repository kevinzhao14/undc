package dungeoncrawler.objects;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.gamestates.GameScreen;
import javafx.scene.image.Image;

public class ShotProjectile implements Movable {
    private Projectile projectile;
    private Image sprite;

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
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
        this.height = height;
        this.width = width;
    }

    public void hit(Entity e) {
        //stop projectile
        velX = 0;

        Room room = ((GameScreen) Controller.getState()).getRoom();

        //hit single monster
        if (e != null) {
            if (e instanceof Monster) {
                ((Monster) e).attackMonster(projectile.getDamage(), true);
            } else {
                //attack other entities
            }
        }

        //splash damage
        if (projectile.isMulti()) {
            for (Monster m : room.getMonsters()) {
                if (m == e) {
                    continue;
                }
                //calculate distance
                double distX = (m.getX() + m.getWidth() / 2) - (posX + width / 2);
                double distY = (m.getY() + m.getHeight() / 2) - (posY + height / 2);
                double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
                //if in range of the blast
                if (dist <= projectile.getSplashRange()) {
                    m.attackMonster(projectile.getDamage(), true);
                }
            }
        }

        //remove projectile
        room.getProjectiles().remove(this);
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

    public double getVelX() {
        return velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Image getSprite() {
        return sprite;
    }

    public Projectile getProjectile() {
        return projectile;
    }
}
