package dungeoncrawler.objects;
import javafx.scene.image.Image;

/**
 * Implementation of the Item abstract data class
 *
 * @author Manas Harbola
 */
public abstract class Item {
    private Image sprite;
    private String name;
    private int maxStackSize;
    private boolean droppable;

    public Item(Image img, String itemName, int stackSize, boolean isDroppable) {
        sprite = img;
        name = itemName;
        maxStackSize = stackSize;
        droppable = isDroppable;
    }
    public Item(Image path, String itemName) {
        sprite = path;
        name = itemName;
    }

    public abstract Item copy();
    public abstract void use();

    public void setSprite(Image img) {
        sprite = img;
    }
    public void setName(String itemName) {
        name = itemName;
    }
    public void setMaxStackSize(int size) {
        maxStackSize = size;
    }
    public void setDroppable(boolean isDroppable) {
        droppable = isDroppable;
    }

    public Image getSprite() {
        return sprite;
    }
    public String getName() {
        return name;
    }
    public int getMaxStackSize() {
        return maxStackSize;
    }
    public boolean isDroppable() {
        return droppable;
    }

    public boolean equals(Item item) {
        return name.equals(item.name) && sprite.getUrl().equals(item.sprite.getUrl())
                && droppable == item.droppable && maxStackSize == item.maxStackSize;
    }

    public String toString() {
        return name + " " + (droppable ? "droppable" : "not droppable") + ", " + maxStackSize;
    }


}
