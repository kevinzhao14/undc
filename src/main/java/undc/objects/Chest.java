package undc.objects;

import javafx.application.Platform;
import javafx.scene.image.Image;
import undc.controllers.GameController;
import undc.gamestates.GameScreen;

public class Chest extends Obstacle implements Interactable {
    private static final Image SPRITE = new Image("textures/chest.png");

    private Inventory contents;

    public Chest(int x, int y, Inventory contents) {
        super(SPRITE, x, y, 32, 32, ObstacleType.SOLID);
        this.contents = contents;
    }

    public boolean interact() {
        GameScreen.getInstance().addOverlay(contents.getGraphicalInventory());
        Platform.runLater(() -> contents.getGraphicalInventory().toggle()); // show the inventory GUI
        GameController.getInstance().pause();
        return true;
    }

    public Inventory getContents() {
        return contents;
    }
}
