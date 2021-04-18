package dungeoncrawler.objects;

import javafx.scene.image.Image;

public class Obstacle implements Movable {

    private double x;
    private double y;
    private double height;
    private double width;
    private ObstacleType type;
    private Image sprite;
    private Item item;

    public Obstacle(double x, double y, double w, double h, ObstacleType type) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        this.type = type;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public void setX(double x) {
        this.y = y;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    public ObstacleType getType() {
        return type;
    }

    public void setType(ObstacleType type) {
        this.type = type;
    }

    public Image getSprite() {
        return sprite;
    }

    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
