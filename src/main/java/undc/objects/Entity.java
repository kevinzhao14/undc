package undc.objects;


import javafx.scene.image.Image;
import org.json.JSONObject;

/**
 * Implementation of the Entity abstract class
 *
 * @author Manas Harbola
 * @version 1.0
 */

public abstract class Entity implements Movable {
    protected int maxHealth;
    protected double health;
    protected double attack;
    protected Image sprite;
    protected double height;
    protected double width;
    private double posX;
    private double posY;
    private double attackCooldown;

    public Entity(int maxHealth, double attack, double height, double width, String sprite) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.height = height;
        this.width = width;
        this.sprite = (sprite == null) ? null : new Image(sprite);
    }

    protected Entity() {
        posX = 0;
        posY = 0;
        attackCooldown = 0;
    }

    protected void copy(Entity copy) {
        copy.maxHealth = this.maxHealth;
        copy.health = this.health;
        copy.attack = this.attack;
        copy.height = this.height;
        copy.width = this.width;
        copy.posX = this.posX;
        copy.posY = this.posY;
        copy.attackCooldown = 0;
        copy.sprite = this.sprite;
    }

    public int getMaxHealth() {
        return maxHealth;
    }
    public double getHealth() {
        return this.health;
    }
    public double getAttack() {
        return this.attack;
    }
    @Override
    public double getHeight() {
        return this.height;
    }
    @Override
    public double getWidth() {
        return this.width;
    }
    @Override
    public double getX() {
        return this.posX;
    }
    @Override
    public double getY() {
        return this.posY;
    }
    @Override
    public void setX(double newX) {
        this.posX = newX;
    }
    @Override
    public void setY(double newY) {
        this.posY = newY;
    }
    public void setHealth(double newHealth) {
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

    public void setSprite(String spriteUrl) {
        this.sprite = new Image(spriteUrl);
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Image getSprite() {
        return sprite;
    }

    public String toString() {
        return "HP: " + health + "/" + maxHealth + " | Pos: " + posX + ", " + posY + " | Size: "
                + height + ", " + width;
    }
}
