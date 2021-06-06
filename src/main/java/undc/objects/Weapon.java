package undc.objects;

import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;

/**
 * Class for handling all weapon types.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class Weapon extends Item {
    //damage per hit
    protected double damage;
    //number of seconds between hits
    protected double attackSpeed;

    /**
     * Full constructor for a weapon.
     * @param name Name of the weapon
     * @param spriteLocation Sprite of the weapon
     * @param damage Damage dealt per hit
     * @param attackSpeed Attack speed of the weapon, in seconds per attack
     * @param droppable whether Weapon is droppable or not
     */
    private Weapon(String name, String spriteLocation, double damage, double attackSpeed,
                  boolean droppable) {
        //super(spriteLocation, name);
        super(new Image(spriteLocation), name, 1, droppable);
        //this.sprite = spriteLocation == null ? null : new ImageView(spriteLocation);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }

    protected Weapon() {

    }

    public Weapon copy() {
        return new Weapon(getName(), getSprite().getUrl(), damage, attackSpeed, isDroppable());
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getDamage() {
        return damage;
    }
    public void use() {

    }
    static Weapon parseJSON(JSONObject o) {
        Weapon weapon = new Weapon();
        try {
            weapon.damage = o.getDouble("damage");
        } catch (JSONException e) {
            Console.error("Invalid value for weapon damage.");
            return null;
        }
        try {
            weapon.attackSpeed = o.getDouble("attackSpeed");
        } catch (JSONException e) {
            Console.error("Invalid value for weapon attack speed.");
            return null;
        }
        return weapon;
    }
}
