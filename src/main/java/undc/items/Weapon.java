package undc.items;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;

/**
 * Class for handling all weapon types.
 */
public class Weapon extends Item {
    //damage per hit
    protected double damage;
    //number of seconds between hits
    protected double attackSpeed;

    protected Weapon() {

    }

    @Override
    public Weapon copy() {
        Weapon weapon = new Weapon();
        copy(weapon);
        weapon.damage = this.damage;
        weapon.attackSpeed = this.attackSpeed;
        return weapon;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getDamage() {
        return damage;
    }

    @Override
    public void use() {

    }

    /**
     * Method used to parse JSON data into a Weapon.
     * @param o JSON object to parse
     * @return Returns a Weapon with the data or null if failed
     */
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
