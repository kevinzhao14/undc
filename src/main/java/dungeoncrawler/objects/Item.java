package dungeoncrawler.objects;

/**
 * Implementation of the Item abstract data class
 *
 * @author Manas Harbola
 */
public abstract class Item {
    private String sprite;
    private String name;
    private int maxStackSize;
    private boolean droppable;

    public Item(String path, String itemName, int stackSize, boolean isDroppable) {
        sprite = path;
        name = itemName;
        maxStackSize = stackSize;
        droppable = isDroppable;
    }
    public Item(String path, String itemName) {
        sprite = path;
        name = itemName;
    }

    public void setSpritePath(String path) {
        sprite = path;
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

    public String getSpritePath() {
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
}
