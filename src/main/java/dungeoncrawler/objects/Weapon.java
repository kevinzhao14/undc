package dungeoncrawler.objects;

import javafx.scene.image.ImageView;

/**
 * Class for handling all weapon types.
 *
 * @version 1.0
 * @author Kevin Zhao
 */
public class Weapon {
    private String name;
    private ImageView sprite;
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
     */
    public Weapon(String name, String spriteLocation, double damage, double attackSpeed) {
        this.name = name;
        this.sprite = new ImageView(spriteLocation);
        this.damage = damage;
        this.attackSpeed = attackSpeed;
    }

    /**
     * Empty constructor for a weapon.
     */
    public Weapon() {
        this("", null, 0, 0);
    }

    /**
     * Getter for the weapon's name.
     * @return The name of the weapon
     */
    public String getName() {
        return this.name;
    }
}