package dungeoncrawler.objects;


import javafx.scene.image.ImageView;

/**
 * Implementation of the Entity abstract class
 *
 * @author Manas Harbola
 * @version 1.0
 */

public abstract class Entity {
    private int maxHealth;
    private int health;
    private double attack;
    private double height;
    private double width;
    private double posX;
    private double posY;
    private double attackCooldown;
    private ImageView node;

    public Entity(int maxHealth, double attack, double height, double width, ImageView node) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.height = height;
        this.width = width;
        this.node = node;
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
    public double getHeight() {
        return this.height;
    }
    public double getWidth() {
        return this.width;
    }
    public double getPosX() {
        return this.posX;
    }
    public double getPosY() {
        return this.posY;
    }

    public void setPosX(double newX) {
        this.posX = newX;
    }
    public void setPosY(double newY) {
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

    public ImageView getNode() {
        return node;
    }

    public void setNode(ImageView node) {
        this.node = node;
    }
}
