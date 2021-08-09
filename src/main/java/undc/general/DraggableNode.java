package undc.general;

import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import undc.command.Vars;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Static class used to make a node draggable.
 */
public class DraggableNode {
    private static final ArrayList<DraggableObject> LIST = new ArrayList<>(); // list of all draggable nodes.

    /**
     * Makes the passed node draggable.
     * @param eventNode Node where the drag events occurs
     * @param restrict Whether to restrict the movement to the bounds of the scene
     * @param dragNodes Node that you wish to drag
     * @return the new DraggableObject
     */
    public static DraggableObject add(Node eventNode, DragDirection direction, boolean restrict, Node... dragNodes) {
        DraggableObject obj = new DraggableObject(eventNode, direction, restrict, dragNodes);
        LIST.add(obj);
        return obj;
    }

    public static DraggableObject add(Node node) {
        return add(node, node);
    }

    public static DraggableObject add(Node eventNode, Node... dragNodes) {
        return add(eventNode, true, dragNodes);
    }

    public static DraggableObject add(Node eventNode, DragDirection direction, Node... dragNodes) {
        return add(eventNode, direction, true, dragNodes);
    }

    public static DraggableObject add(Node eventNode, boolean restrict, Node... dragNodes) {
        return add(eventNode, DragDirection.ALL, restrict, dragNodes);
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
        NONE, START, DRAG, END
    }

    /**
     * Direction the nodes can be dragged in.
     */
    public enum DragDirection {
        VERTICAL, HORIZONTAL, ALL
    }

    /**
     * Listens for drag events.
     */
    public interface Listener {
        void accept(MouseEvent mouseEvent, Event dragEvent);
    }

    /**
     * Class that actually makes a Node draggable and handles dragging.
     */
    public static final class DraggableObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Node> dragNodes = new ArrayList<>();
        private final ArrayList<Listener> listeners = new ArrayList<>();
        private final boolean restrict;
        private final DragDirection direction;

        private double lastMouseX;
        private double lastMouseY;
        private boolean dragging;
        private boolean stop;

        /**
         * Constructor for DraggableObject. Clicking & dragging on eventNode drags the dragNodes.
         * @param eventNode Node that initiates the drag
         * @param dragNodes All the nodes to be dragged
         */
        private DraggableObject(Node eventNode, DragDirection direction, boolean restrict, Node... dragNodes) {
            this.lastMouseX = 0;
            this.lastMouseY = 0;
            this.dragging = false;
            this.stop = false;
            this.eventNode = eventNode;
            this.direction = direction;
            this.restrict = restrict;
            this.dragNodes.addAll(Arrays.asList(dragNodes));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        public boolean addListener(Listener listener) {
            return this.listeners.add(listener);
        }

        public void remove() {
            eventNode.removeEventHandler(MouseEvent.ANY, this);
        }

        public void stop() {
            stop = true;
        }

        @Override
        public final void handle(MouseEvent event) {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
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
                    for (Listener listener : this.listeners) {
                        listener.accept(event, Event.START);
                    }
                }
                // drag the node
                if (this.dragging) {
                    // get the change in mouse position
                    double deltaX = event.getSceneX() - this.lastMouseX;
                    double deltaY = event.getSceneY() - this.lastMouseY;
                    boolean stopped = false;

                    // set the mouse position to the current ones
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();

                    if (stop) {
                        stop = false;
                        return;
                    }

                    // see if any nodes are stopped by the bounds of the screen. If so, set the delta as the max
                    // that node can move and move all nodes by that amount.
                    for (final Node dragNode : this.dragNodes) {
                        double initialX = dragNode.getTranslateX();
                        double initialY = dragNode.getTranslateY();
                        dragNode.setTranslateX(initialX + deltaX);
                        dragNode.setTranslateY(initialY + deltaY);

                        if (restrict) {
                            // make sure the object doesn't get dragged outside the screen/scene
                            Bounds screen = dragNode.localToScene(dragNode.getBoundsInLocal());
                            if (screen.getMaxX() - 50 <= 0) {
                                stopped = true;
                                deltaX -= screen.getMaxX() - 50;
                            } else if (screen.getMinX() + 50 >= Vars.i("gc_screen_width")) {
                                stopped = true;
                                deltaX += Vars.i("gc_screen_width") - screen.getMinX() - 50;
                            }
                            if (screen.getMaxY() - 50 <= 0) {
                                stopped = true;
                                deltaY -= screen.getMaxY() - 50;
                            } else if (screen.getMinY() + 50 >= Vars.i("gc_screen_height")) {
                                stopped = true;
                                deltaY += Vars.i("gc_screen_height") - screen.getMinY() - 50;
                            }
                        }
                        dragNode.setTranslateX(initialX);
                        dragNode.setTranslateY(initialY);
                        if (stopped) {
                            break;
                        }
                    }
                    // actually move the nodes based on the calculated delta values
                    for (final Node dragNode : this.dragNodes) {
                        if (direction == DragDirection.HORIZONTAL || direction == DragDirection.ALL) {
                            dragNode.setTranslateX(dragNode.getTranslateX() + deltaX);
                        }
                        if (direction == DragDirection.VERTICAL || direction == DragDirection.ALL) {
                            dragNode.setTranslateY(dragNode.getTranslateY() + deltaY);
                        }
                    }

                    for (Listener listener : this.listeners) {
                        listener.accept(event, Event.DRAG);
                    }

                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // stop the dragging
                if (this.dragging) {
                    event.consume();
                    this.dragging = false;
                    for (Listener listener : this.listeners) {
                        listener.accept(event, Event.END);
                    }
                }
            }
        }
    }
}