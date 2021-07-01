package undc.graphics;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * Abstract class for graphics that will act as overlays in game.
 */
public abstract class Overlay {
    protected final StackPane root;

    /**
     * Constructor that creates a stackPane with the appearance of a blurred background.
     */
    protected Overlay() {
        root = new StackPane();
        root.setId("root");
        Pane background = new Pane();
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6)");
        root.getChildren().add(background);
    }

    public void toggle() {
        root.setVisible(!root.isVisible());
    }

    public boolean isVisible() {
        return root.isVisible();
    }

    public StackPane getRoot() {
        return root;
    }
}
