package dungeoncrawler.objects;

import dungeoncrawler.controllers.Controller;
import dungeoncrawler.controllers.DataManager;
import dungeoncrawler.gamestates.GameScreen;
import dungeoncrawler.handlers.GameSettings;
import javafx.application.Platform;
import javafx.scene.image.Image;

import java.util.Timer;
import java.util.TimerTask;

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

    public static void addExplosion(Room room, Movable m, double width) {
        if (width > GameSettings.EXPLOSION_MAX_WIDTH) {
            width = GameSettings.EXPLOSION_MAX_WIDTH;
        }

        //draw explosion animation
        Image explosion = new Image(DataManager.EXPLOSION);
        double height = width;
        double x = m.getX() + (m.getWidth() / 2) - (width / 2);
        double y = m.getY() + (m.getHeight() / 2) - (height / 2);
        Obstacle o = new Obstacle(explosion, x, y, width, height, ObstacleType.NONSOLID);
        o.setSprite(explosion);
        room.getObstacles().add(o);
        //remove obstacle after 1 second
        new Timer().schedule(new TimerTask() {
            public void run() {
                room.getObstacles().remove(o);
            }
        }, 1000);
    }


    public void hit(Entity e) {
        //stop projectile
        velX = 0;

        GameScreen screen = (GameScreen) Controller.getState();
        Room room = screen.getRoom();
        Player player = screen.getPlayer();

        //hit single monster
        if (e != null) {
            if (e instanceof Monster) {
                ((Monster) e).attackMonster(projectile.getDamage(), true);
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
            //attack player
            double distX = Math.pow(posX - player.getX() + player.getWidth() / 2, 2);
            double distY = Math.pow(posY - player.getY() + player.getHeight() / 2, 2);
            double dist = Math.sqrt(distX + distY);
            if (dist <= projectile.getSplashRange()) {
                player.setHealth(Math.max(0, player.getHealth() - projectile.getDamage()
                        * GameSettings.PLAYER_ATTACK_SELF_MODIFIER));
                Platform.runLater(() -> screen.updateHud());
                if (player.getHealth() == 0) {
                    Platform.runLater(() -> screen.gameOver());
                }
            }
            //draw explosion animation
            addExplosion(room, this, projectile.getSplashRange() * 2);
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
