package undc.objects;

import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;

/**
 * Class that handles objects launched by RangedWeapons called Projectiles.
 */
public class Projectile {
    private String id;
    private String name;
    private SpriteGroup sprites;
    private double damage;
    //speed of the projectile
    private double speed;
    //how far the projectile goes before stopping/damaging. value of -1 = never stops
    private double range;
    //whether the projectile deals splash damage
    private boolean splash;
    //projectile multi/splash range
    private double splashRange;

    private int height;
    private int width;

    private Projectile() {

    }

    /**
     * Creates a copy of the projectile.
     * @return Projectile that is the copy
     */
    public Projectile copy() {
        Projectile proj = new Projectile();
        proj.id = this.id;
        proj.name = this.name;
        proj.sprites = this.sprites;
        proj.damage = this.damage;
        proj.speed = this.speed;
        proj.range = this.range;
        proj.splash = this.splash;
        proj.splashRange = this.splashRange;
        proj.height = this.height;
        proj.width = this.width;
        return proj;
    }

    public boolean equals(Projectile p) {
        return name.equals(p.name) && damage == p.damage && speed == p.speed && range == p.range
                && splash == p.splash && splashRange == p.splashRange;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRange() {
        return range;
    }

    public boolean isSplash() {
        return splash;
    }

    public double getSplashRange() {
        return splashRange;
    }

    public Image getSpriteRight() {
        return sprites.get(Direction.EAST);
    }

    public Image getSpriteLeft() {
        return sprites.get(Direction.WEST);
    }

    public Image getSpriteUp() {
        return sprites.get(Direction.NORTH);
    }

    public Image getSpriteDown() {
        return sprites.get(Direction.SOUTH);
    }

    public double getDamage() {
        return damage;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    /**
     * Makes JSON data into a Projectile.
     * @param o JSONObject to pull information from.
     * @return Projectile that is created from JSONObject
     */
    public static Projectile parse(JSONObject o) {
        Projectile proj = new Projectile();
        try {
            proj.id = o.getString("id");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile id.");
            return null;
        }
        try {
            proj.name = o.getString("name");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile name.");
            return null;
        }
        try {
            JSONArray sprites = o.getJSONArray("sprites");
            if (sprites.length() != 4) {
                Console.error("Invalid length for projectile sprites.");
                return null;
            }
            proj.sprites = new SpriteGroup(
                new Image(sprites.getString(0)),
                new Image(sprites.getString(1)),
                new Image(sprites.getString(2)),
                new Image(sprites.getString(3))
            );
        } catch (JSONException e) {
            Console.error("Invalid value for projectile sprites.");
            return null;
        } catch (IllegalArgumentException a) {
            Console.error("Invalid value for projectile sprite url.");
            return null;
        }
        try {
            proj.damage = o.getDouble("damage");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile damage.");
            return null;
        }
        try {
            proj.speed = o.getDouble("speed");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile speed.");
            return null;
        }
        try {
            proj.range = o.getDouble("range");
        } catch (JSONException e) {
            proj.range = -1;
        }
        try {
            proj.splash = o.getBoolean("splash");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile splash.");
            return null;
        }
        try {
            proj.splashRange = o.getDouble("splashRange");
        } catch (JSONException e) {
            if (proj.splash) {
                Console.error("Invalid value for projectile splash range.");
                return null;
            } else {
                proj.splashRange = -1;
            }
        }
        try {
            proj.width = o.getInt("width");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile width.");
            return null;
        }
        try {
            proj.height = o.getInt("height");
        } catch (JSONException e) {
            Console.error("Invalid value for projectile height.");
            return null;
        }
        return proj;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Dmg: " + damage + " | Speed: " + speed + " | Range: " + range
                + " | Splash: " + splash + " | sRange: " + splashRange;
    }
}
