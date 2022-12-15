package track.models;

public class MenuItem{
    public int menuId;
    public int quantity;
    public int price;

    public MenuItem(int menuId, int quantity, int price){
        this.menuId = menuId;
        this.quantity = quantity;
        this.price = price;
    }

    //getters and setter left because of time. that is why fields are public
}