package undc.graphics;

import org.json.JSONObject;
import undc.general.Savable;

/**
 * Represents a camera. Used to set the visible viewport.
 */
public class Camera implements Savable {
    private double x;
    private double y;

    public Camera() {
        x = 0;
        y = 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("x", x);
        o.put("y", y);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        return true;
    }
}
