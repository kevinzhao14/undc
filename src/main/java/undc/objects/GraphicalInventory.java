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

    private final Inventory[] inventories;
    private final HBox[] rows;
    private final VBox container;

    private final VBox itemInfo;
    private final Label itemName;
    private final VBox description;

    /**
     * Constructor that creates javafx graphics for the GraphicalInventory.
     * @param inventories Player's inventory that is used to update the graphics with the proper items
     */
    public GraphicalInventory(String titleText, Inventory... inventories) {
        this.inventories = inventories;

        HBox parent = new HBox();
        parent.setId("parent");

        container = new VBox();
        container.setId("container");

        Label title = new Label(titleText);
        title.setId("title");
        container.getChildren().add(title);

        int rownum = 0;
        for (Inventory i : inventories) {
            rownum += i.getRows();
        }

        rows = new HBox[rownum];

        int offset = 0;
        for (Inventory inv : inventories) {
            if (offset > 0) {
                HBox spacer = new HBox();
                spacer.getStyleClass().add("inv-spacer");
                container.getChildren().add(spacer);
            }
            for (int i = 0; i < inv.getRows(); i++) {
                HBox row = new HBox();
                row.getStyleClass().add("row");
                for (int j = 0; j < inv.getCols(); j++) {
                    // VBoxes act as the columns
                    VBox temp = new VBox();
                    row.getChildren().add(temp);
                }
                rows[i + offset] = row;
                container.getChildren().add(row);
            }
            offset += inv.getRows();
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

    public GraphicalInventory(Inventory... inventories) {
        this("Inventory", inventories);
    }

    /**
     * Hides the GraphicalInventory.
     */
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
            Console.warn("Invalid game state.");
        }
        int offset = 0;
        for (Inventory inv : inventories) {
            // access inventory item and put its sprite in the graphical inventory
            for (int i = 0; i < inv.getRows(); i++) {
                InventoryItem[] row = inv.getItems()[i];
                HBox box = rows[i + offset];
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

                    // drop event handler
                    obj.addListener((m, e) -> {
                        // on the start of the drag, move the sprite of the item into a pane so that it is not behind
                        // any of the inventory cells.
                        if (e == DraggableNode.Event.DragStart) {
                            Bounds bounds = image.localToScene(image.getBoundsInLocal());
                            double x = bounds.getMinX();
                            double y = bounds.getMinY();
                            Pane pane = new Pane();
                            pane.getChildren().add(image);
                            image.setX(x);
                            image.setY(y);
                            root.getChildren().add(pane);

                            // at the end of the drag, ie the drop, check where the item was dropped. If its center is
                            // within the bounds of a cell, move it to that cell as long as its empty. If its center is
                            // outside of the entire inventory GUI, drop it in-game. Otherwise, put it back to where it
                            // started.
                        } else if (e == DraggableNode.Event.DragEnd) {
                            // loop through all of the cells to see where it is over.
                            for (int i1 = 0; i1 < rows.length; i1++) {
                                HBox hbox = rows[i1];
                                for (int j1 = 0; j1 < hbox.getChildren().size(); j1++) {
                                    Node node1 = hbox.getChildren().get(j1);
                                    VBox vbox = (VBox) node1;
                                    Bounds ib = vbox.sceneToLocal(image.localToScene(image.getBoundsInLocal()));
                                    double x = ib.getMinX() + image.getFitWidth() / 2;
                                    double y = ib.getMinY() + image.getFitHeight() / 2;

                                    // if the cell VBox contains the mouse's coordinates & the cell is empty, then move
                                    // the item to that cell.
                                    if (vbox.contains(x, y) && vbox.getChildren().size() == 0) {
                                        // move graphically
                                        vbox.getChildren().add(image);
                                        image.setTranslateX(0);
                                        image.setTranslateY(0);
                                        root.getChildren().remove(root.getChildren().size() - 1);

                                        inv.remove(item);

                                        // move in the Inventory. make sure to find which inventory the item goes to.
                                        int rowcount = i1;
                                        for (Inventory inv1 : inventories) {
                                            rowcount -= inv1.getRows();
                                            if (rowcount < 0) {
                                                System.out.println("it " + i1 + " " + rowcount + " " + inv1.getRows());
                                                inv1.add(item, rowcount + inv1.getRows(), j1);
                                                break;
                                            }
                                        }

                                        // update the gui and relationships.
                                        update();

                                        // show the item info popup
                                        populateInfoBox(item);
                                        itemInfo.setVisible(true);
                                        itemInfo.setTranslateX(m.getSceneX() + 25);
                                        itemInfo.setTranslateY(m.getSceneY() + 25);

                                        // update hotbar
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
                                if (!inv.remove(item)) {
                                    Console.error("Failed to remove item to drop.");
                                    return;
                                }
                                GameController.getInstance().drop(item.getItem());
                                GameScreen.getInstance().updateHud();
                            } else {
                                // put it back to original spot
                                square.getChildren().add(image);
                                image.setTranslateX(0);
                                image.setTranslateY(0);
                            }
                            root.getChildren().remove(root.getChildren().size() - 1);
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
            offset += inv.getRows();
        }
    }

    /**
     * Makes inventory visible or not visible.
     */
    public void toggle() {
        // update gui if it's about to show
        if (!root.isVisible()) {
            update();
        }
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
