package undc.entity;

import javafx.scene.image.Image;
import org.json.JSONObject;
import undc.command.Console;
import undc.game.GameObject;
import undc.general.Savable;
import undc.graphics.SpriteGroup;

/**
 * Representation of an Entity object. Entities are objects that are "alive" and can interact with things in the game.
 */

public abstract class Entity extends GameObject implements Savable {
    protected int maxHealth;
    protected double health;
    protected double attack;
    protected double attackCooldown;
    protected boolean invulnerable;
    protected Image sprite;

    /**
     * Constructor.
     * @param maxHealth Max HP of the entity
     * @param attack Base attack damage
     * @param height Height of the entity
     * @param width Width of the entity
     * @param sprite The sprite of the entity (URL)
     */
    public Entity(int maxHealth, double attack, int width, int height, Image sprite) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.width = width;
        this.height = height;
        this.sprite = sprite;
        invulnerable = false;
    }

    /**
     * Constructor used by some children.
     */
    protected Entity() {
        x = 0;
        y = 0;
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
        copy.x = this.x;
        copy.y = this.y;
        copy.attackCooldown = 0;
        copy.sprite = this.sprite;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public double getHealth() {
        return this.health;
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

    public double getAttack() {
        return this.attack;
    }

    public double getAttackCooldown() {
        return attackCooldown;
    }

    public void setAttackCooldown(double attackCooldown) {
        this.attackCooldown = attackCooldown;
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public String toString() {
        return "HP: " + health + "/" + maxHealth + " | Pos: " + x + ", " + y + " | Size: "
                + height + ", " + width;
    }

    /**
     * Loads save data into a Entity object.
     * @param o The data to load
     * @return The corresponding Entity object
     */
    public static Entity parseSaveObject(JSONObject o) {
        try {
            String eclass = o.getString("class");
            switch (eclass) {
                case "Monster":
                    return Monster.parseSaveObject(o);
                case "NPC":
                    return NPC.parseSaveObject(o);
                default:
                    Console.error("Invalid entity type.");
                    return null;
            }
        } catch (Exception e) {
            Console.error("Failed to create entity.");
            return null;
        }
    }
}
