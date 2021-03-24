package dungeoncrawler.objects;

import javafx.scene.layout.HBox;

/**
 * Implementation of the Monster class
 *
 * @author Manas Harbola
 * @version 1.0
 */
public class Monster extends Entity {
    private MonsterType type;
    private double speed;
    private double attackSpeed;
    private HBox healthBar;

    public Monster(int maxHealth, double attack, double speed, double attackSpeed, MonsterType type,
                   int height, int width) {

        //healthBar should be instantiated and assigned in this Monster constructor
        super(maxHealth, attack, height, width);
        this.type = type;
        this.speed = speed;
        this.attackSpeed = attackSpeed;
    }

    public Monster(Monster m) {
        this(m.getMaxHealth(), m.getAttack(), m.speed, m.attackSpeed, m.type, m.getHeight(),
                m.getWidth());
    }

    //need to implement
    public void attackMonster(int dmgAmt) {

    }
}
