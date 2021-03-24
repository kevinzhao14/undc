package dungeoncrawler.objects;

/**
 * Implementation of the Entity abstract class
 *
 * @author Manas Harbola
 * @version 1.0
 */

public abstract class Entity {
    private final int maxHealth;
    private int health;
    private double attack;
    private int height;
    private int width;
    private double posX;
    private double posY;
    private double attackCooldown;

    public Entity(int maxHealth, double attack,
                  int height, int width) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.height = height;
        this.width = width;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public int getHealth() {
        return this.health;
    }
    public double getAttack() {
        return this.attack;
    }
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }
    public double getPosX() {
        return this.posX;
    }
    public double getPosY() {
        return this.posY;
    }

    public void setPosX(int newX) {
        this.posX = newX;
    }
    public void setPosY(int newY) {
        this.posY = newY;
    }
    public void setHealth(int newHealth) {
        if (newHealth < 0 || newHealth > this.maxHealth) {
            throw new IllegalArgumentException("Invalid new health");
        }
        this.health = newHealth;
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(double attackCooldown) {
        this.attackCooldown = attackCooldown;
    }
}
