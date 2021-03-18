package dungeoncrawler.objects;

public class Obstacle {

    private int x;
    private int y;
    private int height;
    private int width;
    private ObstacleType type;
    

    public Obstacle(int x, int y, int w, int h, ObstacleType type) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
        this.type = type;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }
    public int getHeight() {
        return this.height;
    }
    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.y = y;
    }

    public ObstacleType getType() {
        return type;
    }

    public void setType(ObstacleType type) {
        this.type = type;
    }
}
