package dungeoncrawler.handlers;

/**
 * Class that stores all of the game's settings.
 *
 * @author Kevin Zhao, Manas Harbola
 * @version 1.0
 */
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
     * Please note: All calculations except for actually moving JavaFX nodes should be calculated
     * using system 1.
     */
    public static final int FPS = 200;
    //units per second
    public static final double ACCEL = 10.0 / FPS;
    public static final double MAX_VEL = 100.0 / FPS;
    public static final double FRICTION = 15.0 / FPS;
    //pixels per unit
    public static final double PPU = 2.0;
    public static final double PRECISION = 10000;

    //Player stats
    //hitbox
    public static final double PLAYER_WIDTH = 16.730038;
    public static final double PLAYER_HEIGHT = PLAYER_WIDTH * 1.81818182 / 2;
    public static final int PLAYER_HEALTH = 100;
    public static final int PLAYER_ATTACK_RANGE = 35;

    //monster generation settings
    public static final int MIN_MONSTERS = 1;
    public static final int MAX_MONSTERS = 1;

    //monster kill reward settings
    public static final double MONSTER_KILL_GOLD = 30;

    //difficulty modifier
    public static final double MODIFIER_MEDIUM = 1.5;
    public static final double MODIFIER_HARD = 2;

    //how close to a player a monster must be to move towards it
    public static final int MONSTER_MOVE_RANGE = 300;
    public static final int MONSTER_MOVE_MIN = 2;
    //how close to a player a monster must be to attack
    public static final int MONSTER_ATTACK_RANGE = 10;
    //monster reaction time to a player being in range in milliseconds
    public static final int MONSTER_REACTION_TIME = 300;
}
