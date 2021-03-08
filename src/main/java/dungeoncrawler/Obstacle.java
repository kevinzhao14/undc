package dungeoncrawler;

import javafx.scene.shape.Rectangle;

public class Obstacle {

    private int x;
    private int y;
    private int height;
    private int width;

    public Obstacle() {}

    public Obstacle(int x, int y, int h, int w) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
    }

    public Rectangle getObstacle() {
        return new Rectangle(x, y, width, height);
    }


}
