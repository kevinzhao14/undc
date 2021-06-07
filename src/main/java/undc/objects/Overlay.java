package undc.objects;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Abstract class for graphics that will act as overlays in game.
 */
public abstract class Overlay {
    private StackPane root;

    /**
     * Constructor that creates a stackPane with the appearance of a blurred background.
     */
    public Overlay() {
        root = new StackPane();
        Pane background = new Pane();
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6)");
        root.getChildren().add(background);
    }

    public void toggle() {
        root.setVisible(!root.isVisible());
    }

    public StackPane getRoot() {
        return root;
    }

    abstract void update();
}
