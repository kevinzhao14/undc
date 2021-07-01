package undc.game;

import javafx.scene.image.Image;

/**
 * Class that handles doors in game. These objects grant access to rooms.
 */
public class Door extends Obstacle {

    private Room goesTo;

    public Door(double x, double y, int w, int h, Room r, DoorOrientation d) {
        super(new Image("textures/dungeon1-leftdoor.png"), x, y, w, h, ObstacleType.DOOR);
        this.goesTo = r;
    }
    
    public Room getGoesTo() {
        return goesTo;
    }
}
