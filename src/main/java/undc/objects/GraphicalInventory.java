package undc.objects;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import undc.controllers.Console;
import undc.controllers.Controller;
import undc.controllers.GameController;
import undc.gamestates.GameScreen;

public class GraphicalInventory extends Overlay {
    private final Inventory inventory;
    private final HBox[] rows;


    public GraphicalInventory(Inventory inventory) {
        this.inventory = inventory;

        HBox parent = new HBox();
        parent.setId("parent");

        VBox container = new VBox();
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
                image.setSmooth(false);
                image.setFitWidth(60);
                image.setFitHeight(60);
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
