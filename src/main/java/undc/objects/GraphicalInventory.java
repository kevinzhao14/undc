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
import undc.handlers.PopupNode;


/**
 * Class that handles the graphics for the player's inventory.
 */
public class GraphicalInventory extends Overlay {
    private static GraphicalInventory active;

    private final Inventory inventory;
    private final HBox[] rows;
    private final VBox container;

    private final VBox itemInfo;
    private final Label itemName;
    private final VBox description;

    /**
     * Constructor that creates javafx graphics for the GraphicalInventory.
     * @param inventory Player's inventory that is used to update the graphics with the proper items
     */
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
                // VBoxes act as the columns
                VBox temp = new VBox();
                row.getChildren().add(temp);
            }
            rows[i] = row;
            container.getChildren().add(row);
        }

        parent.getChildren().addAll(container);
        root.getChildren().add(parent);
        root.getStylesheets().add("styles/inventory.css");

        // Making graphics for item information hover
        itemInfo = new VBox();
        itemInfo.setVisible(false);
        itemInfo.setId("item-info");

        itemName = new Label("Item Name");
        itemName.setId("item-name");

        description = new VBox();
        description.setId("description");

        itemInfo.getChildren().addAll(itemName, description);
        root.getChildren().add(itemInfo);

        itemInfo.setMaxSize(225, 100);

        toggle();
    }

    public static void hide() {
        if (active != null) {
            active.toggle();
            active = null;
        }
    }

    public static boolean isActive() {
        return active != null;
    }

    /**
     * Looks through the inventory and updates the sprites in the graphical inventory.
     */
    public void update() {
        if (!(Controller.getState() instanceof GameScreen)) {
            Console.error("Invalid game state.");
            return;
        }
        // access inventory item and put its sprite in the graphical inventory
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

                // making inventory draggable
                DraggableNode.remove(square);
                DraggableNode.DraggableObject obj = DraggableNode.add(square, image);

                obj.addListener((m, e) -> {
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

                                    populateInfoBox(item);
                                    itemInfo.setVisible(true);
                                    itemInfo.setTranslateX(m.getSceneX() + 25);
                                    itemInfo.setTranslateY(m.getSceneY() + 25);

                                    GameScreen.getInstance().updateHud();
                                    return;
                                }
                            }
                        }
                        // not put in a spot, check if it's outside of the container to drop
                        Bounds ib = container.sceneToLocal(image.localToScene(image.getBoundsInLocal()));
                        double x = ib.getMinX() + image.getFitWidth() / 2;
                        double y = ib.getMinY() + image.getFitHeight() / 2;
                        if (!container.contains(x, y) && !item.isInfinite()) {
                            if (!inventory.remove(item)) {
                                Console.error("Failed to remove item to drop.");
                                return;
                            }
                            GameController.getInstance().drop(item.getItem());
                            GameScreen.getInstance().updateHud();
                            root.getChildren().remove(root.getChildren().size() - 1);
                        } else {
                            // put it back to original spot
                            square.getChildren().add(image);
                            image.setTranslateX(0);
                            image.setTranslateY(0);
                            root.getChildren().remove(root.getChildren().size() - 1);
                        }
                        update();
                    }
                });

                PopupNode.remove(square);
                PopupNode.PopupObject popup = PopupNode.add(25, 25, square, itemInfo);
                popup.addListener((n, e) -> {
                    if (e == PopupNode.Event.SHOW) {
                        populateInfoBox(item);
                    }
                });

                if (square.getChildren().size() == 0 || !square.getChildren().get(0).equals(image)) {
                    square.getChildren().clear();
                    square.getChildren().add(image);
                }
            }
        }
    }

    /**
     * Makes inventory visible or not visible.
     */
    public void toggle() {
        update();
        super.toggle();
        if (!root.isVisible()) {
            itemInfo.setVisible(false);
        } else {
            active = this;
        }
    }

    /**
     * Provides item info box with descriptors of passed in item.
     * @param invItem InventoryItem that will be described
     */
    private void populateInfoBox(InventoryItem invItem) {
        itemName.setText("");
        description.getChildren().clear();

        Item item = invItem.getItem();

        itemName.setText(item.getName());

        if (item instanceof RangedWeapon) {
            RangedWeapon weapon = (RangedWeapon) item;
            Label projectile = new Label(weapon.getAmmo().getProjectile().getName());
            Label spacer = new Label();
            Label ammo = new Label(weapon.getAmmo().getRemaining()
                    + " / " + weapon.getAmmo().getBackupRemaining() + " Ammo");
            Label damage = new Label(weapon.getAmmo().getProjectile().getDamage() + " Damage");
            Label fireRate = new Label(weapon.getFireRate() + " Fire Rate");

            description.getChildren().addAll(projectile, spacer, ammo, damage, fireRate);
        } else if (item instanceof Weapon) {
            Weapon weapon = (Weapon) item;
            Label damage = new Label(weapon.getDamage() + " Damage");
            Label attackSpeed = new Label(weapon.getAttackSpeed() + " Speed");

            description.getChildren().addAll(damage, attackSpeed);
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            Label type = new Label(potion.getTypeString());
            Label modifier = new Label(potion.getModifierString());

            description.getChildren().addAll(type, modifier);
        } else if (item instanceof Bomb) {
            Bomb bomb = (Bomb) item;
            Label damage = new Label(bomb.getDamage() + " Damage");
            Label radius = new Label(bomb.getRadius() + " Radius");
            Label fuse = new Label(((int) (bomb.getFuse() / 100) / 10) + "s Fuse");

            description.getChildren().addAll(damage, radius, fuse);
        }

        if (invItem.isInfinite()) {
            Label infinite = new Label("Infinite");
            description.getChildren().add(infinite);
        }
    }
}
