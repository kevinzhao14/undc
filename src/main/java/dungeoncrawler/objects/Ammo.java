package dungeoncrawler.objects;

public class Ammo {
    //type of ammo in use
    private Projectile projectile;

    // 30 in mag / 90 ammo left
    private int size;
    private int remaining;
    private int backupMax;
    private int backupRemaining;

    public Ammo(int size, int backupMax, Projectile projectile) {
        this.size = size;
        this.backupMax = backupMax;
        this.remaining = 0;
        this.backupRemaining = 0;
        this.projectile = projectile;
    }

    public Ammo copy() {
        return new Ammo(size, backupMax, projectile != null ? projectile.copy() : null);
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

    public int getBackupMax() {
        return backupMax;
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
}
