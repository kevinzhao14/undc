package dungeoncrawler.objects;

import javafx.scene.image.Image;

public class Ammunition extends Item {
    private int amount;
    private Projectile projectile;

    public Ammunition(String name, String sprite, int amount, Projectile projectile) {
        super(sprite == null ? null : new Image(sprite), name, 1, true);
        this.amount = amount;
        this.projectile = projectile;
    }

    @Override
    public Item copy() {
        Ammunition n = new Ammunition(getName(), null, amount, projectile);
        n.setSprite(getSprite());
        return n;
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
}
