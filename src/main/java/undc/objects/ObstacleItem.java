package undc.objects;

import javafx.scene.image.Image;

public class ObstacleItem extends Obstacle {
    private Item item;

    public ObstacleItem(Image sprite, double x, double y, int w, int h, ObstacleType type) {
        super(sprite, x, y, w, h, type);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
