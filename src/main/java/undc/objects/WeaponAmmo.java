package undc.objects;

import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;
import undc.controllers.DataManager;

public class WeaponAmmo {
    //type of ammo in use
    private Projectile projectile;

    // 30 in mag / 90 ammo left
    private int size;
    private int remaining;
    private int backupSize;
    private int backupRemaining;

    public WeaponAmmo(int size, int backupSize, Projectile projectile) {
        this.size = size;
        this.backupSize = backupSize;
        this.remaining = 0;
        this.backupRemaining = 0;
        this.projectile = projectile;
    }

    private WeaponAmmo() {

    }

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

    static WeaponAmmo parseJSON(JSONObject o) {
        WeaponAmmo ammo = new WeaponAmmo();
        try {
            ammo.projectile = DataManager.PROJECTILES.get(o.getInt("projectile"));
        } catch(JSONException e) {
            Console.error("Invalid value for weapon ammo projectile.");
            return null;
        }
        try {
            ammo.size = o.getInt("size");
        } catch(JSONException e) {
            Console.error("Invalid value for weapon ammo size.");
            return null;
        }
        try {
            ammo.remaining = o.getInt("remaining");
        } catch(JSONException e) {
            ammo.remaining = ammo.size;
        }
        try {
            ammo.backupSize = o.getInt("backupSize");
        } catch(JSONException e) {
            Console.error("Invalid value for weapon ammo backup size.");
            return null;
        }
        try {
            ammo.backupRemaining = o.getInt("backupRemaining");
        } catch(JSONException e) {
            ammo.backupRemaining = ammo.backupSize / 2;
        }
        return ammo;
    }
}
