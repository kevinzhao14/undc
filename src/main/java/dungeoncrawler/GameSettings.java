package dungeoncrawler;

public class GameSettings {
    /*
     *      Player and Object coordinate system
     *
     * -----------------------------------
     *                                   |
     *      --------- < getY() + height  |
     *      |       |                    |
     *      |       |                    |
     *      *-------- < getY()           |
     *      ^       ^                    |
     *      getX()  getX() + width       |
     *                                   |
     * v origin                          |
     * *----------------------------------
     *
     *      However, JavaFX does things a little differently:
     *
     * *----------------------------------
     * ^ origin                          |
     *                                   |
     *      getX()  getX() + width       |
     *      v       v                    |
     *      --------- < getY();          |
     *      |       |                    |
     *      |       |                    |
     *      *-------- < getY() + height  |
     *                                   |
     * -----------------------------------
     *
     * Because of this, we need to convert our Y coordinates to JavaFX's Y coordinates.
     * Please note: All calculations except for actually moving JavaFX nodes should be calculated using
     * system 1.
     */
    public static final int FPS = 60;
    public static final double ACCEL = 1.0;
    public static final double MAX_VEL = 5.0;
    public static final double FRICTION = ACCEL;
    //pixels per unit
    public static final double PPU = 5.0;
    public static final int PRECISION = 10000;

    //Player width and height
    public static final double PLAYER_WIDTH = 10;
    public static final double PLAYER_HEIGHT = 10;
}
