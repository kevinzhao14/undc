package dungeoncrawler.objects;

import javafx.scene.image.Image;

/**
 * Class for handling all weapon types.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class Weapon extends Item {
    //damage per hit
    private double damage;
    //number of seconds between hits
    private double attackSpeed;

    /**
     * Full constructor for a weapon.
     * @param name Name of the weapon
     * @param spriteLocation Sprite of the weapon
     * @param damage Damage dealt per hit
     * @param attackSpeed Attack speed of the weapon, in seconds per attack
     * @param droppable whether Weapon is droppable or not
     */
    public Weapon(String name, String spriteLocation, double damage, double attackSpeed,
                  boolean droppable) {
        //super(spriteLocation, name);
        super(new Image(spriteLocation), name, 1, droppable);
        //this.sprite = spriteLocation == null ? null : new ImageView(spriteLocation);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }

    /**
     * Empty constructor for a weapon.
     */
    public Weapon() {
        this("", null, 0, 0, false);
    }

    public Weapon copy() {
        return new Weapon(getName(), getSprite().getUrl(), damage, attackSpeed, isDroppable());
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public double getDamage() {
        return damage;
    }
    public void use() {

    }
}
