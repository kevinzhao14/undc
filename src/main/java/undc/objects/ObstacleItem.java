package undc.objects;

import javafx.scene.image.*;

public class ObstacleItem extends Obstacle {
    private Item item;

    public ObstacleItem(Image sprite, double x, double y, double w, double h, ObstacleType type) {
        super(sprite, x, y, w, h, type);
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
