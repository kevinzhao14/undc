package undc.objects;

import javafx.scene.image.Image;
import org.json.JSONException;
import org.json.JSONObject;
import undc.controllers.Console;

/**
 * Represents an Item object. Items that can be stored in inventories, dropped, or used.
 */
public abstract class Item {
    protected String id;
    protected Image sprite;
    protected String name;
    protected int maxStackSize;
    protected boolean droppable; // whether the item can be dropped through gameplay
    protected boolean spawnable; // whether the item can be spawned using the spawn command

    protected Item() {
        maxStackSize = 1;
        droppable = false;
    }

    public abstract Item copy();

    /**
     * Method used to clone data to another Item.
     * @param copy Item object to clone to
     */
    protected void copy(Item copy) {
        copy.id = this.id;
        copy.sprite = this.sprite;
        copy.name = this.name;
        copy.maxStackSize = this.maxStackSize;
        copy.droppable = this.droppable;
    }

    public abstract void use();

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
        return "ID: " + id + " | Name: " + name;
    }

    public String getId() {
        return id;
    }

    public boolean isSpawnable() {
        return spawnable;
    }

    /**
     * Method used to parse JSON data into an Item.
     * @param o JSON object to parse
     * @return Returns the respective Item object or null if failed
     */
    public static Item parse(JSONObject o) {
        Item item;
        String type;
        String id;
        String name;
        String sprite;
        int stackSize;
        boolean droppable;
        boolean spawnable;

        try {
            id = o.getString("id");
        } catch (JSONException e) {
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
        try {
            spawnable = o.getBoolean("spawnable");
        } catch (JSONException e) {
            spawnable = true;
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
        item.spawnable = spawnable;

        return item;
    }
}
