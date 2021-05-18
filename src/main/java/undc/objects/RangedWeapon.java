package undc.objects;

import undc.controllers.Controller;
import undc.gamestates.GameScreen;
import javafx.application.Platform;

public class RangedWeapon extends Weapon {
    private WeaponAmmo weaponAmmo;

    //reload time in seconds
    private double reloadTime;
    //seconds between shots
    private double fireRate;

    //statuses
    private boolean isReloading;
    private double delay;

    public RangedWeapon(String name, String sprite, double damage, boolean droppable,
                        double reloadTime, double fireRate, WeaponAmmo weaponAmmo) {
        super(name, sprite, damage, 1, droppable);
        this.reloadTime = reloadTime;
        this.fireRate = fireRate;
        isReloading = false;
        delay = 0;

        this.weaponAmmo = weaponAmmo;
    }
    public RangedWeapon(String name, String sprite, double damage, boolean droppable,
                        double reloadTime, double fireRate) {
        this(name, sprite, damage, droppable, reloadTime, fireRate, null);
        WeaponAmmo weaponAmmo = new WeaponAmmo(0, 0, null);
        this.weaponAmmo = weaponAmmo;
    }

    public RangedWeapon copy() {
        RangedWeapon nw = new RangedWeapon(getName(), getSprite().getUrl(), getDamage(),
                isDroppable(), reloadTime, fireRate, weaponAmmo.copy());
        return nw;
    }

    public void reload() {
        if (weaponAmmo.getRemaining() >= weaponAmmo.getSize()) {
            return;
        }
        isReloading = true;
        delay = reloadTime * 1000;
        Platform.runLater(() -> ((GameScreen) Controller.getState()).updateHud());
    }

    public void finishReloading() {
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

    public double getReloadTime() {
        return reloadTime;
    }

    public double getFireRate() {
        return fireRate;
    }

    public boolean isReloading() {
        return isReloading;
    }

    public void setReloading(boolean reloading) {
        isReloading = reloading;
    }

    public double getDelay() {
        return delay;
    }

    public void setDelay(double delay) {
        this.delay = delay;
    }
}
