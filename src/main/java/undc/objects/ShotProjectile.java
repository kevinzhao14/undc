package undc.objects;

import undc.controllers.Controller;
import undc.controllers.DataManager;
import undc.gamestates.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.handlers.Audio;
import undc.handlers.Vars;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that creates a Projectile that has been fired from a RangedWeapon, called a ShotProjectile. Handles its
 * creation, animation, and functionality.
 */
public class ShotProjectile implements Movable {
    private final Projectile projectile;
    private Image sprite;

    private double posX;
    private double posY;
    private double velX;
    private double velY;
    private final int height;
    private final int width;
    private double distance;

    /**
     * Constructor a ShotProjectile object.
     * @param projectile Projectile that is shot
     * @param posX double x-cord of projectile
     * @param posY double y-cord of projectile
     * @param velX double x-velocity of projectile
     * @param velY double y-velocity of projectile
     * @param width int width of projectile
     * @param height int height of projectile
     */
    public ShotProjectile(Projectile projectile, double posX, double posY, double velX, double velY, int width,
                          int height) {
        this.projectile = projectile;
        this.posX = posX;
        this.posY = posY;
        this.velX = velX;
        this.velY = velY;
        this.height = height;
        this.width = width;
    }

    /**
     * Draws the explosion animation for bombs. Also removes obstacles that get exploded.
     * @param room Room that the explosion occurs in
     * @param m Movable object to get x and y coords from
     * @param width int width of explosion
     */
    public static void addExplosion(Room room, Movable m, int width) {
        if (width > Vars.i("gc_explosion_maxwidth")) {
            width = Vars.i("gc_explosion_maxwidth");
        }

        //draw explosion animation
        Image explosion = new Image(DataManager.EXPLOSION);
        int height = width;
        double x = m.getX() + (m.getWidth() / 2.0) - (width / 2.0);
        double y = m.getY() + (m.getHeight() / 2.0) - (height / 2.0);
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


    /**
     * Handles when a ShotProjectile hits an object.
     * @param e Entity that is hit by the ShotProjectile
     */
    public void hit(Entity e) {
        //stop projectile
        velX = 0;
        velY = 0;

        GameScreen screen = (GameScreen) Controller.getState();
        Room room = screen.getRoom();
        Player player = screen.getPlayer();

        //hit single monster
        if (e != null) {
            if (e instanceof Monster) {
                ((Monster) e).attackMonster(projectile.getDamage());
            }
        }

        //splash damage
        if (projectile.isSplash()) {
            for (Monster m : room.getMonsters()) {
                if (m == e) {
                    continue;
                }
                //calculate distance
                double distX = (m.getX() + m.getWidth() / 2.0) - (posX + width / 2.0);
                double distY = (m.getY() + m.getHeight() / 2.0) - (posY + height / 2.0);
                double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
                //if in range of the blast
                if (dist <= projectile.getSplashRange()) {
                    m.attackMonster(projectile.getDamage());
                }
            }
            //attack player
            double distX = Math.pow(posX - player.getX() + player.getWidth() / 2.0, 2);
            double distY = Math.pow(posY - player.getY() + player.getHeight() / 2.0, 2);
            double dist = Math.sqrt(distX + distY);
            if (dist <= projectile.getSplashRange()) {
                player.setHealth(Math.max(0, player.getHealth() - projectile.getDamage()
                        * Vars.d("sv_self_damage_modifier")));
                Platform.runLater(screen::updateHud);
                if (player.getHealth() == 0) {
                    Platform.runLater(screen::gameOver);
                }
            }
            //draw explosion animation
            Audio.playAudio("rocket_explosion");
            addExplosion(room, this, (int) (projectile.getSplashRange() * 2));
        }

        //remove projectile
        room.getProjectiles().remove(this);
    }

    public void hit() {
        hit(null);
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
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
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
