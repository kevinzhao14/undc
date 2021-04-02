package dungeoncrawler.objects;

public class Inventory {

    private int rows;
    private int columns;
    private InventoryItem[][] items;

    public Inventory(int r, int c) {
        this.rows = r;
        this.columns = c;
        items = new InventoryItem[rows][columns];
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
