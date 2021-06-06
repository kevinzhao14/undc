package undc.objects;

import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;
import undc.controllers.DataManager;

public class Ammunition extends Item {
    private int amount;
    private Projectile projectile;

    private Ammunition(String name, String sprite, int amount, Projectile projectile) {
        super(sprite == null ? null : new Image(sprite), name, 1, true);
        this.amount = amount;
        this.projectile = projectile;
    }

    private Ammunition() {

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

    static Ammunition parseJSON(JSONObject o) {
        Ammunition ammo = new Ammunition();
        try {
            ammo.amount = o.getInt("amount");
        } catch (JSONException e) {
            Console.error("Invalid value for ammunition amount.");
            return null;
        }
        try {
            ammo.projectile = DataManager.PROJECTILES[o.getInt("projectile")];
        } catch(JSONException e) {
            Console.error("Invalid value for ammunition projectile.");
            return null;
        }
        return ammo;
    }
}
