package undc.objects;

import java.util.*;

public class Inventory implements Iterable<InventoryItem> {

    private int rows;
    private int columns;
    private InventoryItem[][] items;
    private int size;

    public Inventory(int r, int c) {
        this.rows = r;
        this.columns = c;
        items = new InventoryItem[rows][columns];
        size = 0;
    }

    public void add(Item item, int quantity) {
        if (full()) {
            return;
        }
        InventoryItem invItem = new InventoryItem(item, quantity);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (items[i][j] == null) {
                    items[i][j] = invItem;
                    size++;
                    return;
                }
            }
        }
    }
    public void add(Item item) {
        add(item, 1);
    }

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
                    if (items[i][j].getQuantity() == 1) {
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

    public void rotate() {
        InventoryItem[] firstrow = items[0];
        for (int i = 0; i < items.length - 1; i++) {
            items[i] = items[i + 1];
        }
        items[items.length - 1] = firstrow;
    }

    public InventoryItem[] getItemsList() {
        InventoryItem[] list = new InventoryItem[size];
        int c = 0;
        for (InventoryItem[] row : items) {
            for (InventoryItem i: row) {
                if (i != null) {
                    list[c++] = i;
                }
            }
        }
        return list;
    }

    public boolean contains(Item item) {
        for (InventoryItem[] row : items) {
            for (InventoryItem i : row) {
                if (i.getItem().equals(item)) {
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

    public class InventoryIterator implements Iterator<InventoryItem> {
        private int row;
        private int col;
        InventoryItem[][] items;

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
