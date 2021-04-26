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
    public static final double ACCEL = 1000.0 / FPS / FPS;
    public static final double MAX_VEL = 100.0 / FPS;
    public static final double FRICTION = ACCEL * 2;
    //pixels per unit
    public static final double PPU = 2.0;
    public static final double PRECISION = 10000;

    //Player stats
    //hitbox
    public static final double PLAYER_WIDTH = 16;
    public static final double PLAYER_HEIGHT = PLAYER_WIDTH;
    public static final int PLAYER_HEALTH = 100;
    public static final int PLAYER_ATTACK_RANGE = 20;
    public static final double PLAYER_ATTACK_SELF_MODIFIER = 0.75;
    public static final double PLAYER_PICKUP_RANGE = 10;
    public static final double PLAYER_FIST_DAMAGE = 1;
    public static final double PLAYER_FIST_COOLDOWN = 0.5;

    public static final int INVENTORY_ROWS = 2;
    public static final int INVENTORY_COLUMNS = 5;

    //monster generation settings
    public static final int MIN_MONSTERS = 1;
    public static final int MAX_MONSTERS = 1;

    //obstacles
    public static final int OBSTACLES_MIN = 2;
    public static final int OBSTACLES_MAX = 5;
    public static final double OBSTACLES_DISTANCE = 64;

    //monster kill reward settings
    public static final double MONSTER_KILL_GOLD = 20;

    //monster drop reward settings
    public static final int MIN_ITEM_DROP = 1;
    public static final int MAX_ITEM_DROP = 2;
    public static final double DROP_ITEM_SPRITE_SCALE = 0.75;
    public static final double DROP_ITEM_DISTANCE = 25;

    //difficulty modifier
    public static final double MODIFIER_MEDIUM = 1.5;
    public static final double MODIFIER_HARD = 2;

    //how close to a player a monster must be to move towards it
    public static final int MONSTER_MOVE_RANGE = 300;
    public static final int BOSS_MOVE_RANGE = MONSTER_MOVE_RANGE * 2;
    public static final int MONSTER_MOVE_MIN = 10;
    //how close to a player a monster must be to attack
    public static final int MONSTER_ATTACK_RANGE = 10;
    //monster reaction time to a player being in range in milliseconds
    public static final int MONSTER_REACTION_TIME = 300;
    public static final int BOSS_REACTION_TIME = MONSTER_REACTION_TIME / 2;
    public static final int MONSTER_FADE_TIME = 400;

    public static final double EXPLOSION_MAX_WIDTH = 75;

    //pixels
    public static final int MONSTER_HEALTHBAR_HEIGHT = 5;

    public static final int CANVAS_PADDING = 150;

    public static final double EFFECT_INDICATOR_SCALE = 1.5;
}
