package dungeoncrawler.objects;

import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.LinkedList;

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
    private double reaction;
    private LinkedList<double[]> moveQueue;

    private double deathProgress = 1; // -> 0

    public Monster(int maxHealth, double attack, double speed, double attackSpeed, MonsterType type,
                   double height, double width, ImageView node, double sH, double sW) {

        //healthBar should be instantiated and assigned in this Monster constructor
        super(maxHealth, attack, height, width, node, sH, sW);
        this.type = type;
        this.speed = speed;
        this.attackSpeed = attackSpeed;
        this.reaction = -1;
        moveQueue = new LinkedList<>();
    }

    public Monster(Monster m, double modifier) {
        this((int) (m.getMaxHealth() * modifier), m.getAttack() * modifier, m.speed,
                m.attackSpeed, m.type, m.getHeight(), m.getWidth(),
                new ImageView(m.getNode().getImage().getUrl()), m.getSpriteHeight(), m.getSpriteWidth());
    }

    //need to implement
    public void attackMonster(double dmgAmt) {
        this.setHealth(Math.max(0, this.getHealth() - dmgAmt));
        if (getHealth() <= 0) {
            System.out.println("Monster slain.");
        }
    }

    public double getDeathProgress() {
        return deathProgress;
    }

    public void setDeathProgress(double deathProgress) {
        this.deathProgress = deathProgress;
    }

    public double getReaction() {
        return reaction;
    }

    public void setReaction(double reaction) {
        this.reaction = reaction;
    }

    public double getSpeed() {
        return speed;
    }

    public String toString() {
        return "Type: " + type + " | Speed: " + speed + " | Attack: " + getAttack() + " " + attackSpeed + " " + super.toString();
    }

    public LinkedList<double[]> getMoveQueue() {
        return moveQueue;
    }

    public double getAttackSpeed() {
        return attackSpeed;
    }

    public MonsterType getType() {
        return this.type;
    }
}
