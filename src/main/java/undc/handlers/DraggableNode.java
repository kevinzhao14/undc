package undc.handlers;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static class used to make a node draggable.
 */
public class DraggableNode {
    private static final ArrayList<DraggableObject> LIST = new ArrayList<>(); // list of all draggable nodes.

    /**
     * Makes a node draggable.
     * @param node Node that is to be made draggable
     */
    public static void add(Node node) {
        LIST.add(new DraggableObject(node));
    }

    /**
     * Class that actually makes a Node draggable and handles dragging.
     */
    private static final class DraggableObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Node> dragNodes = new ArrayList<>();

        private double lastMouseX = 0;
        private double lastMouseY = 0;
        private boolean dragging = false;

        /**
         * Constructor for DraggableObject. Clicking & dragging on eventNode drags the dragNodes.
         * @param eventNode Node that initiates the drag
         * @param dragNodes All the nodes to be dragged
         */
        DraggableObject(Node eventNode, Node... dragNodes) {
            this.eventNode = eventNode;
            this.dragNodes.addAll(Arrays.asList(dragNodes));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        DraggableObject(Node node) {
            this(node, node);
        }

        @Override
        public final void handle(MouseEvent event) {
            // press event to start dragging
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                // if the click event happens on the node, then we set the mouse's current position and begin dragging.
                if (!this.dragging && this.eventNode.contains(event.getX(), event.getY())) {
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();
                    event.consume();
                    this.dragging = true;
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                // drag the node
                if (this.dragging) {
                    // get the change in mouse position
                    final double deltaX = event.getSceneX() - this.lastMouseX;
                    final double deltaY = event.getSceneY() - this.lastMouseY;

                    // translate every node based on the mouse move
                    for (final Node dragNode : this.dragNodes) {
                        final double initialTranslateX = dragNode.getTranslateX();
                        final double initialTranslateY = dragNode.getTranslateY();
                        dragNode.setTranslateX(initialTranslateX + deltaX);
                        dragNode.setTranslateY(initialTranslateY + deltaY);
                    }

                    // set the mouse position to the current ones
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();

                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // stop the dragging
                if (this.dragging) {
                    event.consume();
                    this.dragging = false;
                }
            }
        }
    }
}