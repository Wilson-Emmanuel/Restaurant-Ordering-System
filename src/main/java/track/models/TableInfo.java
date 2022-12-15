package track.models;

public class TableInfo{
    public long totalAmount;
    public int orderSent;
    public int orderServed;
    public int tableId;
    public TableInfo(int tableId){
        this.tableId = tableId;
    }
}
