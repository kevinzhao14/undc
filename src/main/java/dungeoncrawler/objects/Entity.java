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
    private int attack;
    private int height;
    private int width;
    private int posX;
    private int posY;

    public Entity(int maxHealth, int attack,
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
    public int getAttack() {
        return this.attack;
    }
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }
    public int getPosX() {
        return this.posX;
    }
    public int getPosY() {
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
}
