package undc.objects;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.controllers.GameController;
import undc.gamestates.GameScreen;
import undc.handlers.DraggableNode;

public class GraphicalInventory extends Overlay {
    private final Inventory inventory;
    private final HBox[] rows;
    private final VBox container;


    public GraphicalInventory(Inventory inventory) {
        this.inventory = inventory;

        HBox parent = new HBox();
        parent.setId("parent");

        container = new VBox();
        container.setId("container");

        Label title = new Label("Inventory");
        title.setId("title");
        container.getChildren().add(title);

        rows = new HBox[inventory.getRows()];

        for (int i = 0; i < inventory.getRows(); i++) {
            HBox row = new HBox();
            row.getStyleClass().add("row");
            for (int j = 0; j < inventory.getCols(); j++) {
                VBox temp = new VBox();
                row.getChildren().add(temp);
            }
            rows[i] = row;
            container.getChildren().add(row);
        }

        parent.getChildren().add(container);
        root.getChildren().add(parent);
        root.getStylesheets().add("styles/inventory.css");

        toggle();
    }

    public void update() {
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Invalid game state.");
            return;
        }
        for (int i = 0; i < inventory.getRows(); i++) {
            InventoryItem[] row = inventory.getItems()[i];
            HBox box = rows[i];
            if (box.getChildren().size() != row.length) {
                Console.error("Invalid inventory.");
                return;
            }
            for (int j = 0; j < row.length; j++) {
                InventoryItem item = row[j];
                Node node = box.getChildren().get(j);
                if (!(node instanceof VBox)) {
                    Console.error("Invalid inventory row.");
                    return;
                }
                VBox square = (VBox) node;
                if (item == null) {
                    square.getChildren().clear();
                    continue;
                }
                ImageView image = new ImageView(item.getItem().getSprite());
                image.setPickOnBounds(true);
                image.setSmooth(false);
                image.setFitWidth(60);
                image.setFitHeight(60);

                DraggableNode.remove(square);
                DraggableNode.DraggableObject obj = DraggableNode.add(square, image);

                obj.addListener((n, e) -> {
                   if (e == DraggableNode.Event.DragStart) {
                       Bounds bounds = image.localToScene(image.getBoundsInLocal());
                       double x = bounds.getMinX();
                       double y = bounds.getMinY();
                       Pane pane = new Pane();
                       pane.getChildren().add(image);
                       image.setX(x);
                       image.setY(y);
                       root.getChildren().add(pane);
                   } else if (e == DraggableNode.Event.DragEnd) {
                       for (int i1 = 0; i1 < rows.length; i1++) {
                           HBox hbox = rows[i1];
                           for (int j1 = 0; j1 < hbox.getChildren().size(); j1++) {
                               Node node1 = hbox.getChildren().get(j1);
                               VBox vbox = (VBox) node1;
                               Bounds ib = vbox.sceneToLocal(image.localToScene(image.getBoundsInLocal()));
                               double x = ib.getMinX() + image.getFitWidth() / 2;
                               double y = ib.getMinY() + image.getFitHeight() / 2;
                               if (vbox.contains(x, y) && vbox.getChildren().size() == 0) {
                                   // move graphically
                                   vbox.getChildren().add(image);
                                   image.setTranslateX(0);
                                   image.setTranslateY(0);
                                   root.getChildren().remove(root.getChildren().size() - 1);

                                   inventory.remove(item);
                                   inventory.add(item, i1, j1);

                                   update();
                                   GameScreen.getInstance().updateHud();
                                   return;
                               }
                           }
                       }
                       // not put in a spot, check if it's outside of the container to drop
                       Bounds ib = container.sceneToLocal(image.localToScene(image.getBoundsInLocal()));
                       double x = ib.getMinX() + image.getFitWidth() / 2;
                       double y = ib.getMinY() + image.getFitHeight() / 2;
                       if (!container.contains(x, y)) {
                           if (!inventory.remove(item)) {
                               Console.error("Failed to remove item to drop.");
                               return;
                           }
                           GameController.getInstance().drop(item.getItem());
                           root.getChildren().remove(root.getChildren().size() - 1);
                       } else {
                           // put it back to original spot
                           square.getChildren().add(image);
                           image.setTranslateX(0);
                           image.setTranslateY(0);
                           root.getChildren().remove(root.getChildren().size() - 1);
                       }
                       update();
                       GameScreen.getInstance().updateHud();
                   }
                });

                if (square.getChildren().size() == 0 || !square.getChildren().get(0).equals(image)) {
                    square.getChildren().clear();
                    square.getChildren().add(image);
                }
            }
        }
    }

    public void toggle() {
        update();
        super.toggle();
    }
}
