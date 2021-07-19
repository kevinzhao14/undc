package undc.items;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.general.Controller;
import undc.entity.Player;
import undc.game.ObstacleItem;
import undc.game.ObstacleType;
import undc.graphics.GameScreen;
import undc.general.Audio;

/**
 * Class that represents a Bomb item. Can be placed to deal damage to all entities within a specific range.
 */
public class Bomb extends Item {
    private double damage;
    private double radius;
    private double fuse;
    private double livefuse;

    private Bomb() {
        livefuse = -1;
    }

    @Override
    public Bomb copy() {
        Bomb bomb = new Bomb();
        copy(bomb);
        bomb.damage = this.damage;
        bomb.radius = this.radius;
        bomb.fuse = this.fuse;
        return bomb;
    }

    @Override
    public void use() {
        GameScreen screen = (GameScreen) Controller.getState();
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
        int width = 20;
        int height = 20;
        double x = player.getX() + player.getWidth() / 2.0 - width / 2.0;
        double y = player.getY() + player.getHeight() / 2.0 - height / 2.0;
        ObstacleItem o = new ObstacleItem(getSprite(), x, y, width, height, ObstacleType.NONSOLID);
        Bomb timer = copy();
        o.setItem(timer);
        screen.getRoom().getObstacles().add(o);

        //start fuse
        Audio.playAudio("fuse");
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

    /**
     * Method used to parse JSON data into a Bomb object.
     * @param o JSON data to parse
     * @return Returns a Bomb object with the data or null if failed
     */
    static Bomb parseJSON(JSONObject o) {
        Bomb bomb = new Bomb();
        try {
            bomb.damage = o.getDouble("damage");
        } catch (JSONException e) {
            Console.error("Invalid value for bomb damage.");
            return null;
        }
        try {
            bomb.radius = o.getDouble("radius");
        } catch (JSONException e) {
            Console.error("Invalid value for bomb radius.");
            return null;
        }
        try {
            bomb.fuse = o.getDouble("fuse");
        } catch (JSONException e) {
            Console.error("Invalid value for bomb fuse.");
            return null;
        }
        bomb.livefuse = -1;
        return bomb;
    }
}
