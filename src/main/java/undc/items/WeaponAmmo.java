package undc.items;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.command.DataManager;

/**
 * Represents the ammunition data of a RangedWeapon.
 */
public class WeaponAmmo {
    //type of ammo in use
    private Projectile projectile;

    // 30 in mag / 90 ammo left
    private int size;
    private int remaining;
    private int backupSize;
    private int backupRemaining;

    /**
     * Constructor.
     * @param size Size of a magazine
     * @param backupSize Size of the backup ammo
     * @param projectile Projectile used
     */
    public WeaponAmmo(int size, int backupSize, Projectile projectile) {
        this.size = size;
        this.backupSize = backupSize;
        this.remaining = 0;
        this.backupRemaining = 0;
        this.projectile = projectile;
    }

    private WeaponAmmo() {

    }

    /**
     * Method used to copy the WeaponAmmo into a new object.
     * @return Returns a new WeaponAmmo with the same data
     */
    public WeaponAmmo copy() {
        WeaponAmmo ammo = new WeaponAmmo();
        ammo.projectile = this.projectile;
        ammo.size = this.size;
        ammo.remaining = this.remaining;
        ammo.backupSize = this.backupSize;
        ammo.backupRemaining = this.backupRemaining;
        return ammo;
    }

    public int getSize() {
        return size;
    }

    public int getRemaining() {
        return remaining;
    }

    public void setRemaining(int remaining) {
        this.remaining = remaining;
    }

    public int getBackupSize() {
        return backupSize;
    }

    public int getBackupRemaining() {
        return backupRemaining;
    }

    public void setBackupRemaining(int backupRemaining) {
        this.backupRemaining = backupRemaining;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * Method used to parse JSON data into a WeaponAmmo.
     * @param o JSON object to parse
     * @return Returns a WeaponAmmo with the data or null if failed
     */
    static WeaponAmmo parseJSON(JSONObject o) {
        WeaponAmmo ammo = new WeaponAmmo();
        try {
            ammo.projectile = DataManager.PROJECTILES.get(o.getString("projectile"));
        } catch (JSONException e) {
            Console.error("Invalid value for weapon ammo projectile.");
            return null;
        }
        try {
            ammo.size = o.getInt("size");
        } catch (JSONException e) {
            Console.error("Invalid value for weapon ammo size.");
            return null;
        }
        try {
            ammo.remaining = o.getInt("remaining");
        } catch (JSONException e) {
            ammo.remaining = ammo.size;
        }
        try {
            ammo.backupSize = o.getInt("backupSize");
        } catch (JSONException e) {
            Console.error("Invalid value for weapon ammo backup size.");
            return null;
        }
        try {
            ammo.backupRemaining = o.getInt("backupRemaining");
        } catch (JSONException e) {
            ammo.backupRemaining = ammo.backupSize / 2;
        }
        return ammo;
    }
}
