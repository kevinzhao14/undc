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
    private int speed;
    private int attackSpeed;
    private HBox healthBar;

    public Monster(int maxHealth, int attack, int height,
                   int width, MonsterType type, int speed,
                   int attackSpeed) {

        //healthBar should be instantiated and assigned in this Monster constructor
        super(maxHealth, attack, height, width);
        this.type = type;
        this.speed = speed;
        this.attackSpeed = attackSpeed;
    }

    //need to implement
    public void attackMonster(int dmgAmt) {

    }
}
