package track.models;

public class OrderItem{
    public int menuId;
    public int quantity;
    public int tableId;

    public OrderItem(int menuId, int quantity, int tableId){
        this.menuId = menuId;
        this.quantity = quantity;
        this.tableId = tableId;
    }

    //getters and setter left because of time. that is why fields are public
}