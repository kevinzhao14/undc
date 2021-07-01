package undc.item;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;

/**
 * Class that represents an ammunition item. Is used to give players extra ammunition.
 */
public class Ammunition extends Item {
    private int amount;
    private Projectile projectile;

    private Ammunition() {

    }

    @Override
    public Item copy() {
        Ammunition ammo = new Ammunition();
        copy(ammo);
        ammo.amount = amount;
        ammo.projectile = this.projectile;
        return ammo;
    }

    @Override
    public void use() {

    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * Method used to parse some JSON data into an Ammunition object.
     * @param o JSON object to parse
     * @return Returns an Ammunition object with the data or null if failed
     */
    static Ammunition parseJSON(JSONObject o) {
        Ammunition ammo = new Ammunition();
        try {
            ammo.amount = o.getInt("amount");
        } catch (JSONException e) {
            Console.error("Invalid value for ammunition amount.");
            return null;
        }
        try {
            ammo.projectile = DataManager.PROJECTILES.get(o.getString("projectile"));
        } catch (JSONException e) {
            Console.error("Invalid value for ammunition projectile.");
            return null;
        }
        return ammo;
    }
}
