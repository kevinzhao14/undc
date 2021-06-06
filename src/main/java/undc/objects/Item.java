package undc.objects;
import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;

/**
 * Implementation of the Item abstract data class
 *
 * @author Manas Harbola
 */
public abstract class Item {
    protected int id;
    protected Image sprite;
    protected String name;
    protected int maxStackSize;
    protected boolean droppable;

    protected Item() {

    }

    public Item(int id, Image img, String itemName, int stackSize, boolean isDroppable) {
        this.id = id;
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

    protected void copy(Item copy) {
        copy.id = -this.id;
        copy.sprite = this.sprite;
        copy.name = this.name;
        copy.maxStackSize = this.maxStackSize;
        copy.droppable = this.droppable;
    }

    public abstract void use();

    public void setSprite(Image img) {
        sprite = img;
    }
    public void setName(String itemName) {
        name = itemName;
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

    public int getId() {
        return id;
    }

    public static Item parse(JSONObject o) {
        Item item;
        String type;
        int id;
        String name;
        String sprite;
        int stackSize;
        boolean droppable;

        try {
            id = o.getInt("id");
        } catch(JSONException e) {
            Console.error("Invalid value for item id.");
            return null;
        }
        try {
            type = o.getString("type");
        } catch (JSONException e) {
            Console.error("Invalid value for item type");
            return null;
        }
        try {
            name = o.getString("name");
        } catch (JSONException e) {
            Console.error("Invalid value for item name.");
            return null;
        }
        try {
            sprite = o.getString("sprite");
        } catch (JSONException e) {
            Console.error("Invalid value for item sprite.");
            return null;
        }
        try {
            stackSize = o.getInt("stackSize");
        } catch (JSONException e) {
            stackSize = 1;
        }
        try {
            droppable = o.getBoolean("droppable");
        } catch (JSONException e) {
            droppable = false;
        }

        switch (type.toLowerCase()) {
            case "weapon":
                item = Weapon.parseJSON(o);
                break;
            case "potion":
                item = Potion.parseJSON(o);
                break;
            case "bomb":
                item = Bomb.parseJSON(o);
                break;
            case "rangedweapon":
                item = RangedWeapon.parseJSON(o);
                break;
            case "ammunition":
                item = Ammunition.parseJSON(o);
                break;
            case "key":
                item = Key.parseJSON(o);
                break;
            default:
                Console.error("Invalid item type.");
                return null;
        }

        if (item == null) {
            return null;
        }

        item.id = id;
        item.name = name;
        item.sprite = new Image(sprite);
        item.maxStackSize = stackSize;
        item.droppable = droppable;

        return item;
    }

    private static boolean tc(TcObj o, String str) {
        try {
            o.run();
            return true;
        } catch (JSONException e) {
            Console.error("Invalid value for " + str + ".");
            return false;
        }
    }

    private abstract class TcObj {
        public abstract void run();
    }
}
