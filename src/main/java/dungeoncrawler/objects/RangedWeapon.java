package dungeoncrawler.objects;

public class RangedWeapon extends Weapon{
    private Ammo ammo;

    //reload time in seconds
    private double reloadTime;
    //seconds between shots
    private double fireRate;

    //statuses
    private boolean isReloading;
    private double delay;

    public RangedWeapon(String name, String sprite, double damage, boolean droppable,
                        double reloadTime, double fireRate, Ammo ammo) {
        super(name, sprite, damage, 0, droppable);
        this.reloadTime = reloadTime;
        this.fireRate = fireRate;
        isReloading = false;
        delay = 0;

        this.ammo = ammo;
    }
    public RangedWeapon(String name, String sprite, double damage, boolean droppable,
                        double reloadTime, double fireRate) {
        this(name, sprite, damage, droppable, reloadTime, fireRate, null);
        Projectile proj = new Projectile("None", 0, 0, false, 0);
        Ammo ammo = new Ammo(0, 0, proj);
        this.ammo = ammo;
    }

    public Ammo getAmmo() {
        return ammo;
    }

    public void setAmmo(Ammo ammo) {
        this.ammo = ammo;
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
