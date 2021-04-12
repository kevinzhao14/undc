package dungeoncrawler.objects;

public class Inventory {

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

    public void add(Item item) {
        if (full()) {
            return;
        }
        InventoryItem invItem = new InventoryItem(item, 1);
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

    public boolean full() {
        return size >= rows * columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public InventoryItem[][] getItems() {
        return items;
    }

    public void setItems(InventoryItem[][] items) {
        this.items = items;
    }
}
