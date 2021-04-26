package dungeoncrawler.objects;

import javafx.scene.image.Image;

public class Projectile {
    private String name;
    private Image spriteRight;
    private Image spriteLeft;
    private Image spriteUp;
    private Image spriteDown;
    private double damage;

    //speed of the projectile
    private double speed;
    //how far the projectile goes before stopping/damaging
    private double range;
    //whether the projectile hits only 1 target or multiple, if false paired with -1 range
    private boolean isMulti;
    //projectile multi/splash range
    private double splashRange;

    public Projectile(String name, double damage, double speed, double range,
                      boolean isMulti, double splashRange) {
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.range = range;
        this.isMulti = isMulti;
        this.splashRange = splashRange;
    }

    public Projectile(String name, String[] sprites, double damage, double speed, double range,
                      boolean isMulti, double splashRange) {
        this(name, damage, speed, range, isMulti, splashRange);
        setSprites(sprites[0], sprites[1], sprites[2], sprites[3]);
    }

    public Projectile copy() {
        Projectile p = new Projectile(name, damage, speed, range, isMulti, splashRange);
        p.setSprites(spriteLeft.getUrl(), spriteUp.getUrl(), spriteRight.getUrl(),
                spriteDown.getUrl());
        return p;
    }

    public void setSprites(String left, String up, String right, String down) {
        spriteLeft = new Image(left);
        spriteUp = left.equals(up) ? spriteLeft : new Image(up);
        spriteRight = left.equals(right) ? spriteLeft : (up.equals(right) ? spriteUp
                : new Image(right));
        spriteDown = left.equals(down) ? spriteLeft : (up.equals(down) ? spriteUp
                : (right.equals(down) ? spriteRight : new Image(down)));
    }

    public boolean equals(Projectile p) {
        return name.equals(p.name) && damage == p.damage && speed == p.speed && range == p.range
                && isMulti == p.isMulti && splashRange == p.splashRange;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }

    public double getRange() {
        return range;
    }

    public boolean isMulti() {
        return isMulti;
    }

    public double getSplashRange() {
        return splashRange;
    }

    public Image getSpriteRight() {
        return spriteRight;
    }

    public Image getSpriteLeft() {
        return spriteLeft;
    }

    public Image getSpriteUp() {
        return spriteUp;
    }

    public Image getSpriteDown() {
        return spriteDown;
    }

    public double getDamage() {
        return damage;
    }
}
