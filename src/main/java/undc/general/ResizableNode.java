package undc.general;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static class used to make Regions resizable.
 */
public class ResizableNode {

    private static final ArrayList<ResizableObject> LIST = new ArrayList<>();

    /**
     * Makes a Region resizable and adds it to the list.
     * @param eventNode Node that initiates the resizing
     * @param direction Direction the eventNode should be allowed to resize
     * @param resizeNodes The nodes that are to be resized
     */
    public static void add(Node eventNode, ResizeDirection direction, Region... resizeNodes) {
        LIST.add(new ResizableObject(eventNode, direction, resizeNodes));
    }

    /**
     * Enum representing the direction(s) a resizable object can be resized in.
     */
    public enum ResizeDirection {
        HORIZONTAL, VERTICAL, ALL, H_DRAGV, V_DRAGH
    }

    /**
     * Class that actually makes a Region resizable and handles resizing.
     */
    private static final class ResizableObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Region> resizeNodes = new ArrayList<>();
        private final ResizeDirection direction;

        private double lastMouseX;
        private double lastMouseY;
        private boolean resizing;

        /**
         * Constructor for a ResizableObject. eventNode initiates the resize, while resizeNodes are the nodes that
         * actually get resized.
         * @param eventNode Node that initiates the resizing
         * @param direction Direction the eventNode should be allowed to resize
         * @param resizeNodes The nodes that are to be resized
         */
        ResizableObject(final Node eventNode, ResizeDirection direction, final Region[] resizeNodes) {
            this.lastMouseX = 0;
            this.lastMouseY = 0;
            this.resizing = false;
            this.eventNode = eventNode;
            this.direction = direction;
            this.resizeNodes.addAll(Arrays.asList(resizeNodes));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        @Override
        public void handle(MouseEvent event) {
            // press event to start resizing
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                // if the click event happens on the node, then we set the mouse's current position and begin resizing.
                if (!this.resizing && this.eventNode.contains(event.getX(), event.getY())) {
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();
                    event.consume();
                    this.resizing = true;
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                // resize the node
                if (this.resizing) {
                    // get the change in mouse position
                    final double deltaX = event.getSceneX() - this.lastMouseX;
                    final double deltaY = event.getSceneY() - this.lastMouseY;

                    // translate every node based on the mouse move
                    for (final Region dragNode : this.resizeNodes) {
                        final double initialWidth = dragNode.getPrefWidth();
                        final double initialHeight = dragNode.getPrefHeight();
                        if (direction == ResizeDirection.HORIZONTAL || direction == ResizeDirection.ALL
                                || direction == ResizeDirection.H_DRAGV) {
                            dragNode.setPrefWidth(initialWidth + deltaX);
                        }
                        if (direction == ResizeDirection.VERTICAL || direction == ResizeDirection.ALL
                                || direction == ResizeDirection.V_DRAGH) {
                            dragNode.setPrefHeight(initialHeight + deltaY);
                        }
                        if (direction == ResizeDirection.H_DRAGV) {
                            dragNode.setTranslateY(dragNode.getTranslateY() + deltaY);
                        }
                        if (direction == ResizeDirection.V_DRAGH) {
                            dragNode.setTranslateX(dragNode.getTranslateX() + deltaX);
                        }
                    }

                    // set the mouse position to the current ones
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();

                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // stop the dragging
                if (this.resizing) {
                    event.consume();
                    this.resizing = false;
                }
            }
        }
    }
}
