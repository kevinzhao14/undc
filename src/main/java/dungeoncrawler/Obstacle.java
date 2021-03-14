package dungeoncrawler;
public class Obstacle {

    private int x;
    private int y;
    private int height;
    private int width;
    

    public Obstacle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.height = h;
        this.width = w;
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
}
