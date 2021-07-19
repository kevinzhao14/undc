package undc.graphics;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import undc.command.Vars;

/**
 * Abstract class that provides a frame work for different game states.
 */
public abstract class GameState {
    protected final Scene scene;

    /**
     * Constructor for a GameState.
     * @param width Width of the GameState
     * @param height Height of the GameState
     */
    public GameState(int width, int height) {
        scene = new Scene(new Pane(), width, height);

        // resizing the scene sets the size cvars so that different states remain the same size.
        scene.widthProperty().addListener((obs, oldVal, newVal) -> Vars.set("gc_screen_width", newVal.intValue()));
        scene.heightProperty().addListener((obs, oldVal, newVal) -> Vars.set("gc_screen_height", newVal.intValue()));
    }

    public Scene getScene() {
        return scene;
    }
}
