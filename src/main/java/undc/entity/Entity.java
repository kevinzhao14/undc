package undc.entity;

import javafx.scene.image.Image;
import undc.general.Movable;
import undc.general.Savable;
import undc.graphics.SpriteGroup;

/**
 * Representation of an Entity object. Entities are objects that are "alive" and can interact with things in the game.
 */

public abstract class Entity implements Movable, Savable {
    protected int maxHealth;
    protected double health;
    protected double attack;
    protected Image sprite;
    protected int height;
    protected int width;
    protected double posX;
    protected double posY;
    protected double attackCooldown;
    protected boolean invulnerable;

    /**
     * Constructor.
     * @param maxHealth Max HP of the entity
     * @param attack Base attack damage
     * @param height Height of the entity
     * @param width Width of the entity
     * @param sprite The sprite of the entity (URL)
     */
    public Entity(int maxHealth, double attack, int width, int height, String sprite) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.width = width;
        this.height = height;
        this.sprite = (sprite == null) ? null : new Image(sprite);
        invulnerable = false;
    }

    /**
     * Constructor used by some children.
     */
    protected Entity() {
        posX = 0;
        posY = 0;
        attackCooldown = 0;
        sprite = SpriteGroup.NONE;
    }

    /**
     * Method to copy an Entity's data to another entity.
     * @param copy The Entity to copy the data to
     */
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
    public int getHeight() {
        return this.height;
    }

    @Override
    public int getWidth() {
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

    /**
     * Sets the health of the Entity.
     * @param newHealth New health of the Entity
     */
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

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Image getSprite() {
        return sprite;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public String toString() {
        return "HP: " + health + "/" + maxHealth + " | Pos: " + posX + ", " + posY + " | Size: "
                + height + ", " + width;
    }
}
