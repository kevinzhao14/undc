package undc.handlers;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Class that handles the creation of nodes that will display when hovering over specific nodes.
 */
public class PopupNode {
    private static final ArrayList<PopupObject> LIST = new ArrayList<>();

    /**
     * Adds the passed in Node to the ArrayList of PopupObjects LIST.
     * @param offsetX int to be used in the constructor for a PopupObject to add to LIST
     * @param offsetY int to be used in the constructor for a PopupObject to add to LIST
     * @param eventNode Node to be used in the constructor for a PopupObject to add to LIST
     * @param popupNodes Node(s) to be used in the constructor for a PopupObject to add to LIST
     * @return PopupObject that is created by using the passed in the values of the method
     */
    public static PopupObject add(int offsetX, int offsetY, Node eventNode, Node... popupNodes) {
        PopupObject obj = new PopupObject(offsetX, offsetY, eventNode, popupNodes);
        LIST.add(obj);
        return obj;
    }

    /**
     * Removes the passed in Node from the ArrayList LIST of PopupObjects.
     * @param eventNode Node to remove from LIST
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
     * Enumerations for the different type of Events regarding popups.
     */
    public enum Event {
        NONE, SHOW, MOVE, HIDE
    }

    /**
     * Listener that looks for PopupObjects and drag events.
     */
    public interface Listener {
        void accept(PopupObject popup, Event dragEvent);
    }

    /**
     * Class that handles the functionality behind enabling a node to popup when hovered over.
     */
    public static final class PopupObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Node> popupNodes = new ArrayList<>();
        private final ArrayList<Listener> popupListeners = new ArrayList<>();

        private double lastMouseX = 0;
        private double lastMouseY = 0;
        private final int offsetX;
        private final int offsetY;
        private boolean showing = false;

        /**
         * Creates a PopupObject.
         * @param offsetX int amount offset the popup's x position
         * @param offsetY int amount offset the popup's y position
         * @param eventNode Node which triggers the popup
         * @param popupNodes Node(s) that will popup
         */
        PopupObject(int offsetX, int offsetY, Node eventNode, Node... popupNodes) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.eventNode = eventNode;
            this.popupNodes.addAll(Arrays.asList(popupNodes));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        public final void addListener(Listener listener) {
            this.popupListeners.add(listener);
        }

        public void remove() {
            eventNode.removeEventHandler(MouseEvent.ANY, this);
        }

        @Override
        public final void handle(MouseEvent event) {
            if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
                if (!this.showing && this.eventNode.contains(event.getX(), event.getY())) {
                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();
                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
                if (!this.showing) {
                    this.showing = true;
                    for (Listener listener : this.popupListeners) {
                        listener.accept(this, Event.SHOW);
                    }
                    for (Node popupNode : this.popupNodes) {
                        popupNode.setVisible(true);
                        popupNode.setTranslateX(this.lastMouseX + this.offsetX);
                        popupNode.setTranslateY(this.lastMouseY + this.offsetY);
                    }
                }
                if (this.showing) {
                    final double deltaX = event.getSceneX() - this.lastMouseX;
                    final double deltaY = event.getSceneY() - this.lastMouseY;

                    for (final Node popupNode : this.popupNodes) {
                        final double initialTranslateX = popupNode.getTranslateX();
                        final double initialTranslateY = popupNode.getTranslateY();
                        popupNode.setTranslateX(initialTranslateX + deltaX);
                        popupNode.setTranslateY(initialTranslateY + deltaY);
                    }

                    this.lastMouseX = event.getSceneX();
                    this.lastMouseY = event.getSceneY();

                    for (Listener listener : this.popupListeners) {
                        listener.accept(this, Event.MOVE);
                    }

                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED
                    || event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (this.showing) {
                    event.consume();
                    this.showing = false;
                    for (Node popupNode : this.popupNodes) {
                        popupNode.setVisible(false);
                    }
                    for (Listener listener : this.popupListeners) {
                        listener.accept(this, Event.HIDE);
                    }
                }
            }
        }
    }
}
