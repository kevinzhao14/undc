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
    public static DraggableObject add(Node node) {
        DraggableObject obj = new DraggableObject(node);
        LIST.add(obj);
        return obj;
    }

    /**
     * Makes the passed node draggable.
     * @param eventNode Node where the drag events occurs
     * @param dragNodes Node that you wish to drag
     * @return the new DraggableObject
     */
    public static DraggableObject add(Node eventNode, Node... dragNodes) {
        DraggableObject obj = new DraggableObject(eventNode, dragNodes);
        LIST.add(obj);
        return obj;
    }

    /**
     * Makes a previously draggable node not draggable.
     * @param eventNode The node you want to remove the draggable event from.
     */
    public static void remove(Node eventNode) {
        for (int i = 0; i < LIST.size(); i++) {
            if (LIST.get(i).eventNode.equals(eventNode)) {
                LIST.get(i).remove();
                LIST.remove(i);
                break;
            }
        }
    }

    /**
     * Enum with the different type of DragEvents to be handled.
     */
    public enum Event {
        None, DragStart, Drag, DragEnd
    }

    /**
     * Listens for drag events.
     */
    public interface Listener {
        void accept(DraggableObject draggableNature, Event dragEvent);
    }

    /**
     * Class that actually makes a Node draggable and handles dragging.
     */
    public static final class DraggableObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Node> dragNodes = new ArrayList<>();
        private final ArrayList<Listener> dragListeners = new ArrayList<>();

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

        public final boolean addListener(Listener listener) {
            return this.dragListeners.add(listener);
        }

        public void remove() {
            eventNode.removeEventHandler(MouseEvent.ANY, this);
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
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                if (!this.dragging) {
                    this.dragging = true;
                    for (Listener listener : this.dragListeners) {
                        listener.accept(this, Event.DragStart);
                    }
                }
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

                    for (Listener listener : this.dragListeners) {
                        listener.accept(this, Event.Drag);
                    }

                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // stop the dragging
                if (this.dragging) {
                    event.consume();
                    this.dragging = false;
                    for (Listener listener : this.dragListeners) {
                        listener.accept(this, Event.DragEnd);
                    }
                }
            }
        }
    }
}