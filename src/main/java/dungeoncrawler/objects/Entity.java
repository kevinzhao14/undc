package dungeoncrawler.objects;


import javafx.scene.image.Image;

/**
 * Implementation of the Entity abstract class
 *
 * @author Manas Harbola
 * @version 1.0
 */

public abstract class Entity implements Movable {
    private int maxHealth;
    private double health;
    private double attack;
    private double height;
    private double width;
    private double posX;
    private double posY;
    private double attackCooldown;
    private Image node;
    private double spriteWidth;
    private double spriteHeight;

    public Entity(int maxHealth, double attack, double height, double width, String node) {
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.attack = attack;
        this.height = height;
        this.width = width;
        this.node = (node == null) ? null : new Image(node);
        this.spriteHeight = 1;
        this.spriteWidth = 1;
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

    public String getNode() {
        return node.getUrl();
    }

    public void setNode(String node) {
        this.node = new Image(node);
    }

    public Image getImage() {
        return node;
    }

    public void setImage(Image image) {
        this.node = image;
    }

    public String toString() {
        return "HP: " + health + "/" + maxHealth + " | Pos: " + posX + ", " + posY + " | Size: "
                + height + ", " + width + " | Node: " + node;
    }

    public double getSpriteWidth() {
        return spriteWidth;
    }

    public double getSpriteHeight() {
        return spriteHeight;
    }
}
