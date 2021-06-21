package undc.objects;

import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;

/**
 * Represents an Obstacle object. Obstacles are physical, static objects in the game that entities can interact with.
 */
public class Obstacle implements Movable {
    private String id;
    private String name;
    private double x;
    private double y;
    private int height;
    private int width;
    private ObstacleType type;
    private Image sprite;

    /**
     * Constructor.
     * @param sprite Sprite of the obstacle
     * @param x X position of the obstacle
     * @param y Y position of the obstacle
     * @param w Width of the obstacle
     * @param h Height of the obstacle
     * @param type Type of the obstacle
     */
    public Obstacle(Image sprite, double x, double y, int w, int h, ObstacleType type) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        this.type = type;
        this.sprite = sprite;
    }

    private Obstacle() {

    }

    public Obstacle copy() {
        Obstacle o = new Obstacle();
        o.id = this.id;
        o.name = this.name;
        o.x = 0;
        o.y = 0;
        o.height = this.height;
        o.width = this.width;
        o.type = this.type;
        o.sprite = this.sprite;
        return o;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
        return this.width;
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    public ObstacleType getType() {
        return type;
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    /**
     * Method used to parse JSON data into an Obstacle.
     * @param o JSON object to parse
     * @return Returns an Obstacle with the data or null if failed
     */
    public static Obstacle parse(JSONObject o) {
        Obstacle obs = new Obstacle();
        try {
            obs.id = o.getString("id");
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle id.");
            return null;
        }
        try {
            obs.name = o.getString("name");
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle name.");
            return null;
        }
        try {
            obs.type = ObstacleType.valueOf(o.getString("type").toUpperCase());
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle type.");
            return null;
        } catch (IllegalArgumentException a) {
            Console.error("Invalid type for obstacle type.");
            return null;
        }
        try {
            obs.sprite = new Image(o.getString("sprite"));
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle sprite.");
            return null;
        } catch (IllegalArgumentException a) {
            Console.error("Invalid value for obstacle sprite url.");
            return null;
        }
        try {
            obs.width = o.getInt("width");
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle width.");
            return null;
        }
        try {
            obs.height = o.getInt("height");
        } catch (JSONException e) {
            Console.error("Invalid value for obstacle height.");
            return null;
        }
        return obs;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "ID: " + id + " | Name: " + name + " | Type: " + type + " | Dim: " + width + ", " + height;
    }
}
