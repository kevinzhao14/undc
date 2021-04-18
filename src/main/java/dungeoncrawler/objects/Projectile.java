package dungeoncrawler.objects;

import javafx.scene.image.Image;

public class Projectile {
    private String name;
    private Image sprite;

    //speed of the projectile
    private double speed;
    //how far the projectile goes before stopping/damaging
    private double range;
    //whether the projectile hits only 1 target or multiple, if false paired with -1 range
    private boolean isMulti;
    //projectile multi/splash range
    private double splashRange;

    public Projectile(String name, String sprite, double speed, double range, boolean isMulti,
                      double splashRange) {
        this.name = name;
        this.sprite = new Image(sprite);
        this.speed = speed;
        this.range = range;
        this.isMulti = isMulti;
        this.splashRange = splashRange;
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

    public Image getSpriteImage() {
        return sprite;
    }

    public void setSpriteImage(Image sprite) {
        this.sprite = sprite;
    }
}
