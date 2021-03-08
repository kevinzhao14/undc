package dungeoncrawler;

public class Obstacle {
    private int obstacleHeight;
    private int obstacleWidth;
    private int obstacleXPos; //Upper-right hand corner x-position of obstacle
    private int obstacleYPos; //Upper-right hand corner y-position of obstacle

    public Obstacle(int height, int width, int xPos, int yPos) {
        this.obstacleHeight = height;
        this.obstacleWidth = width;
        this.obstacleXPos = xPos;
        this.obstacleYPos = yPos;
    }

    public int getHeight() {
        return this.obstacleHeight;
    }
    public int getWidth() {
        return this.obstacleWidth;
    }
    public int getXPos() {
        return this.obstacleXPos;
    }
    public int getYPos() {
        return this.obstacleYPos;
    }
}
