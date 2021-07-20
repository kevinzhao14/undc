package undc.game;

import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;
import undc.general.Movable;
import undc.general.Savable;

/**
 * Represents an Obstacle object. Obstacles are physical, static objects in the game that entities can interact with.
 */
public class Obstacle implements Movable, Savable {
    private String id;
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

    protected Obstacle() {
        this.type = ObstacleType.SOLID;
    }

    /**
     * Makes a copy of the static obstacle.
     * @return Obstacle that is the copy
     */
    public Obstacle copy() {
        Obstacle o = new Obstacle();
        o.id = id;
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

    public void setType(ObstacleType type) {
        this.type = type;
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("id", id);
        o.put("x", x);
        o.put("y", y);
        o.put("width", width);
        o.put("height", height);
        o.put("type", type.toString());
        o.put("class", "Obstacle");
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            String oclass = o.getString("class");
            if (oclass.equals("Obstacle")) {
                id = o.getString("id");
                sprite = DataManager.OBSTACLES.get(id).getSprite();
            }
            x = o.getDouble("x");
            y = o.getDouble("y");
            width = o.getInt("width");
            height = o.getInt("height");
            type = ObstacleType.valueOf(o.getString("type"));
        } catch (Exception e) {
            Console.error("Failed to load Obstacle.");
            return false;
        }
        return true;
    }

    /**
     * Loads save data into an Obstacle object.
     * @param o The data to load
     * @return The corresponding Obstacle object
     */
    public static Obstacle parseSaveObject(JSONObject o) {
        try {
            Obstacle obs;
            String oclass = o.getString("class");
            switch (oclass) {
                case "ObstacleItem":
                    obs = ObstacleItem.parseSaveObject(o);
                    break;
                case "Chest":
                    obs = Chest.parseSaveObject(o);
                    break;
                default:
                    obs = new Obstacle();
                    break;
            }
            return obs;
        } catch (Exception e) {
            Console.error("Failed to create Obstacle.");
            return null;
        }
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

    public String toString() {
        return "Type: " + type + " | Dim: " + width + ", " + height;
    }
}
