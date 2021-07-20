package undc.inventory;

import org.json.JSONArray;
import org.json.JSONObject;
import undc.command.Console;
import undc.items.Item;
import undc.general.Savable;

import java.util.Iterator;

/**
 * Class that handles the games inventory/item storing system.
 * Is used by other classes to access items that the player possesses.
 */
public class Inventory implements Iterable<InventoryItem>, Savable {
    private int rows;
    private int columns;
    private InventoryItem[][] items;
    private int size;
    private GraphicalInventory graphicalInventory;

    /**
     * Makes a 2D array with specific rows and columns that represents an inventor.
     * @param r number of rows in the 2D array
     * @param c number of columns in the 2D array
     */
    public Inventory(int r, int c) {
        this.rows = r;
        this.columns = c;
        items = new InventoryItem[rows][columns];
        size = 0;
        graphicalInventory = new GraphicalInventory(this);
    }

    public void add(Item item, int quantity) {
        add(new InventoryItem(item, quantity));
    }

    public void add(Item item) {
        add(item, 1);
    }

    /**
     * Adds an inventory item to the first available spot in the array.
     * @param item InventoryItem to be added to the inventory
     */
    public void add(InventoryItem item) {
        if (item == null) {
            return;
        }
        if (full()) {
            return;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (items[i][j] == null) {
                    add(item, i, j);
                    return;
                }
            }
        }
    }

    /**
     * Adds an InventoryItem to a specific location in the array.
     * @param item InventoryItem being added
     * @param row int representing the row to add to
     * @param col int representing the column to add to
     */
    public void add(InventoryItem item, int row, int col) {
        if (item == null) {
            Console.error("Cannot add null to inventory.");
            return;
        }
        if (items[row][col] != null) {
            Console.error("Could not add, slot taken.");
            return;
        }
        items[row][col] = item;
        size++;
    }

    /**
     * Removes an Item from the inventory array.
     * @param item Item to be removed
     * @return the removed Item item
     */
    public boolean remove(Item item) {
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (items[i][j] == null) {
                    continue;
                }
                if (items[i][j].getItem() == item) {
                    if (items[i][j].isInfinite()) {
                        return true;
                    } else if (items[i][j].getQuantity() == 1) {
                        items[i][j] = null;
                        size--;
                    } else {
                        items[i][j].setQuantity(items[i][j].getQuantity() - 1);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes an InventoryItem from the inventory array.
     * @param item InventoryItem to be removed
     * @return the removed InventoryItem
     */
    public boolean remove(InventoryItem item) {
        if (size == 0) {
            return false;
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (items[i][j] == null) {
                    continue;
                }
                if (items[i][j] == item) {
                    items[i][j] = null;
                    size--;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Shifts each row of items up by 1.
     */
    public void rotate() {
        InventoryItem[] firstrow = items[0];
        System.arraycopy(items, 1, items, 0, items.length - 1);
        items[items.length - 1] = firstrow;
    }

    /**
     * Creates a list of all the InventoryItems in the inventory array.
     * @return InventoryItem[] that is the list of items in the array
     */
    public InventoryItem[] getItemsList() {
        InventoryItem[] list = new InventoryItem[size];
        int c = 0;
        for (InventoryItem[] row : items) {
            for (InventoryItem i : row) {
                if (i != null) {
                    list[c++] = i;
                }
            }
        }
        return list;
    }

    /**
     * Determines if a specific Item is in the inventory array.
     * @param item Item to look for
     * @return boolean of whether or not the passed in item is in the inventory array
     */
    public boolean contains(Item item) {
        for (InventoryItem[] row : items) {
            for (InventoryItem i : row) {
                if (i != null && i.getItem().equals(item)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean full() {
        return size >= rows * columns;
    }

    public InventoryItem[][] getItems() {
        return items;
    }

    public void setItems(InventoryItem[][] items) {
        this.items = items;
    }

    public int getCols() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    @Override
    public Iterator<InventoryItem> iterator() {
        return new InventoryIterator(items);
    }

    /**
     * Accessor method for the GraphicalInventory. Ensures only one is created.
     * @return GraphicalInventory representing the player's inventory
     */
    public GraphicalInventory getGraphicalInventory() {
        return graphicalInventory;
    }

    public void setGraphicalInventory(GraphicalInventory graphicalInventory) {
        this.graphicalInventory = graphicalInventory;
    }

    @Override
    public JSONObject saveObject() {
        JSONObject o = new JSONObject();
        o.put("rows", rows);
        o.put("columns", columns);
        o.put("size", size);
        JSONArray items = new JSONArray();
        for (InventoryItem[] irow : this.items) {
            JSONArray row = new JSONArray();
            for (InventoryItem invitem : irow) {
                if (invitem == null) {
                    row.put("null");
                } else {
                    row.put(invitem.saveObject());
                }
            }
            items.put(row);
        }
        o.put("items", items);
        return o;
    }

    @Override
    public boolean parseSave(JSONObject o) {
        try {
            rows = o.getInt("rows");
            columns = o.getInt("columns");
            size = o.getInt("size");
            JSONArray itms = o.getJSONArray("items");
            items = new InventoryItem[rows][columns];
            for (int i = 0; i < itms.length(); i++) {
                JSONArray row = itms.getJSONArray(i);
                for (int j = 0; j < row.length(); j++) {
                    if (row.get(j) instanceof String) {
                        items[i][j] = null;
                    } else {
                        items[i][j] = new InventoryItem(null, 0);
                        if (!items[i][j].parseSave(row.getJSONObject(j))) {
                            Console.error("Failed to load inventory item.");
                            return false;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Console.error("Failed to load Inventory.");
            return false;
        }
        return true;
    }

    /**
     * Inner class that handles iterating through the 2D array items.
     */
    public static class InventoryIterator implements Iterator<InventoryItem> {
        private int row;
        private int col;
        private final InventoryItem[][] items;

        /**
         * Constructor that sets up the iterator to begin iterating from row and column 0 through the 2D array items.
         * @param items InventoryItem[][] to iterate through
         */
        public InventoryIterator(InventoryItem[][] items) {
            row = 0;
            col = 0;
            this.items = items;
        }

        @Override
        public boolean hasNext() {
            return row < items.length && col < items[0].length;
        }

        @Override
        public InventoryItem next() {
            InventoryItem i = items[row][col];
            col++;
            if (col >= items[0].length) {
                row++;
                col = 0;
            }
            return i;
        }
    }
}
