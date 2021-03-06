package undc.game.objects;

import org.json.JSONObject;
import undc.command.Console;
import undc.game.Room;
import undc.game.calc.Direction;
import undc.general.Controller;
import undc.command.DataManager;
import undc.entity.Entity;
import undc.entity.Monster;
import undc.entity.Player;
import undc.graphics.SpriteGroup;
import undc.items.Projectile;
import undc.general.Savable;
import undc.graphics.GameScreen;
import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.general.Audio;
import undc.command.Vars;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Class that creates a Projectile that has been fired from a RangedWeapon, called a ShotProjectile. Handles its
 * creation, animation, and functionality.
 */
public class ShotProjectile extends GameObject implements Savable {
    private final Projectile projectile;

    private Image sprite;
    private double velX;
    private double velY;
    private double distance;

    /**
     * Constructor a ShotProjectile object.
     * @param projectile Projectile that is shot
     * @param x double x-cord of projectile
     * @param y double y-cord of projectile
     * @param velX double x-velocity of projectile
     * @param velY double y-velocity of projectile
     */
    public ShotProjectile(Projectile projectile, double x, double y, double velX, double velY) {
        this.projectile = projectile;
        this.x = x;
        this.y = y;
        this.velX = velX;
        this.velY = velY;
        this.height = projectile.getHeight();
        this.width = projectile.getWidth();
    }

    /**
     * Draws the explosion animation for bombs. Also removes obstacles that get exploded.
     * @param room Room that the explosion occurs in
     * @param m Movable object to get x and y coords from
     * @param width int width of explosion
     */
    public static void addExplosion(Room room, GameObject m, int width) {
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
            for (Entity t : room.getEntities()) {
                if (t == e || !(t instanceof Monster)) {
                    continue;
                }
                //calculate distance
                double distX = (t.getX() + t.getWidth() / 2.0) - (x + width / 2.0);
                double distY = (t.getY() + t.getHeight() / 2.0) - (y + height / 2.0);
                double dist = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
                //if in range of the blast
                if (dist <= projectile.getSplashRange()) {
                    ((Monster) t).attackMonster(projectile.getDamage());
                }
            }
            //attack player
            double distX = Math.pow(x - player.getX() + player.getWidth() / 2.0, 2);
            double distY = Math.pow(y - player.getY() + player.getHeight() / 2.0, 2);
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

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("projectile", projectile.getId());
        o.put("posX", x);
        o.put("posY", y);
        o.put("velX", velX);
        o.put("velY", velY);
        o.put("distance", distance);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            x = o.getDouble("posX");
            y = o.getDouble("posY");
            velX = o.getDouble("velX");
            velY = o.getDouble("velY");
            distance = o.getDouble("distance");
            SpriteGroup sprites = projectile.getSprites();
            if (velX < 0) {
                sprite = sprites.get(Direction.WEST);
            } else if (velX > 0) {
                sprite = sprites.get(Direction.EAST);
            } else if (velY > 0) {
                sprite = sprites.get(Direction.NORTH);
            } else {
                sprite = sprites.get(Direction.SOUTH);
            }
        } catch (Exception e) {
            Console.error("Failed to load Shot Projectile.");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into a ShotProjectile object.
     * @param o The data to load
     * @return The corresponding ShotProjectile object
     */
    public static ShotProjectile parseSaveObject(JSONObject o) {
        try {
            Projectile proj = DataManager.PROJECTILES.get(o.getString("projectile"));
            return new ShotProjectile(proj, 0, 0, 0, 0);
        } catch (Exception e) {
            Console.error("Failed to create Shot Projectile.");
            return null;
        }
    }
}
