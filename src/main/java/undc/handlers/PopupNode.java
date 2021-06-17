package undc.handlers;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Arrays;

public class PopupNode {
    private static final ArrayList<PopupObject> LIST = new ArrayList<>();

    public static PopupObject add(Node eventNode, Node... popupNodes) {
        PopupObject obj = new PopupObject(eventNode, popupNodes);
        LIST.add(obj);
        return obj;
    }

    public static void remove(Node eventNode) {
        for (int i = 0; i < LIST.size(); i++) {
            if (LIST.get(i).eventNode.equals(eventNode)) {
                LIST.get(i).remove();
                LIST.remove(i);
                break;
            }
        }
    }

    public enum Event {
        NONE, SHOW, MOVE, HIDE
    }

    public interface Listener {
        void accept(PopupObject popup, Event dragEvent);
    }

    public static final class PopupObject implements EventHandler<MouseEvent> {
        private final Node eventNode;
        private final ArrayList<Node> popupNodes = new ArrayList<>();
        private final ArrayList<Listener> popupListeners = new ArrayList<>();

        private double lastMouseX = 0;
        private double lastMouseY = 0;
        private boolean showing = false;

        PopupObject(Node eventNode, Node... popupNodes) {
            this.eventNode = eventNode;
            this.popupNodes.addAll(Arrays.asList(popupNodes));
            this.eventNode.addEventHandler(MouseEvent.ANY, this);
        }

        public final boolean addListener(Listener listener) {
            return this.popupListeners.add(listener);
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
                    this.showing = true;
                    for (Listener listener : this.popupListeners) {
                        listener.accept(this, Event.SHOW);
                    }
                    for (Node popupNode : this.popupNodes) {
                        popupNode.setVisible(true);
                    }
                    event.consume();
                }
            } else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
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
            } else if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
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
