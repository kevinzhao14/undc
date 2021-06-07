package undc.objects;

public class GraphicalInventory extends Overlay {

    private Inventory inventory;

    public GraphicalInventory(Inventory inventory) {
        this.inventory = inventory;

        toggle();
    }

    public void update() {

    }
}
