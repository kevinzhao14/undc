package undc.items;

import org.json.JSONException;
import org.json.JSONObject;
import undc.command.Console;
import undc.general.Controller;
import undc.graphics.GameScreen;
import javafx.application.Platform;

/**
 * Represents a ranged weapon. Ranged weapons are weapons that fire a projectile and can deal damage from a distance.
 */
public class RangedWeapon extends Weapon {
    private WeaponAmmo weaponAmmo;
    //reload time in seconds
    private double reloadTime;
    //seconds between shots
    private double fireRate;
    //statuses
    private boolean isReloading;
    private double delay;

    private RangedWeapon() {
        isReloading = false;
        delay = 0;
    }

    @Override
    public RangedWeapon copy() {
        RangedWeapon weapon = new RangedWeapon();
        copy(weapon);
        weapon.weaponAmmo = this.weaponAmmo.copy();
        weapon.reloadTime = this.reloadTime;
        weapon.fireRate = this.fireRate;
        return weapon;
    }

    /**
     * Method used to start reloading the weapon.
     */
    public void startReload() {
        if (weaponAmmo.getRemaining() >= weaponAmmo.getSize()) {
            return;
        }
        isReloading = true;
        delay = reloadTime * 1000;
        if (reloadTime == 0) {
            finishReload();
        }
        Platform.runLater(() -> ((GameScreen) Controller.getState()).updateHud());
    }

    /**
     * Method used at the conclusion of reloading to fill the ammo capacity.
     */
    public void finishReload() {
        int change = weaponAmmo.getSize() - weaponAmmo.getRemaining();
        weaponAmmo.setRemaining(weaponAmmo.getSize());
        weaponAmmo.setBackupRemaining(weaponAmmo.getBackupRemaining() - change);
        isReloading = false;
        Platform.runLater(() -> ((GameScreen) Controller.getState()).updateHud());
    }

    public WeaponAmmo getAmmo() {
        return weaponAmmo;
    }

    public void setAmmo(WeaponAmmo weaponAmmo) {
        this.weaponAmmo = weaponAmmo;
    }

    public double getFireRate() {
        return fireRate;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }

    /**
     * Method used to parse JSON data into a RangedWeapon.
     * @param o JSON object to parse
     * @return Returns a RangedWeapon with the data, null otherwise
     */
    static RangedWeapon parseJSON(JSONObject o) {
        RangedWeapon weapon = new RangedWeapon();
        try {
            weapon.reloadTime = o.getDouble("reloadTime");
        } catch (JSONException e) {
            Console.error("Invalid value for ranged weapon reload time.");
            return null;
        }
        try {
            weapon.fireRate = o.getDouble("fireRate");
        } catch (JSONException e) {
            Console.error("Invalid value for ranged weapon fire rate.");
            return null;
        }
        try {
            weapon.damage = o.getDouble("damage");
        } catch (JSONException e) {
            weapon.damage = 1;
        }
        try {
            weapon.attackSpeed = o.getDouble("attackSpeed");
        } catch (JSONException e) {
            weapon.attackSpeed = 1;
        }
        try {
            weapon.weaponAmmo = WeaponAmmo.parseJSON(o.getJSONObject("ammo"));
        } catch (JSONException e) {
            Console.error("Invalid value for ranged weapon ammo.");
            return null;
        }
        return weapon;
    }
}
