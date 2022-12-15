package track.subsystems;

import track.Service;
import track.events.Checkout;
import track.events.OrderReady;
import track.events.OrderReceived;
import track.models.OrderItem;

public class Sub4 implements OrderReceived, OrderReady, Checkout {

    private Service service;
    public static final int SUBSYSTEM_ID = 4;

    public Sub4(Service service){
        this.service = service;
    }

    @Override
    public void checkOut(int tableId) {

        long totalAmount = service.checkoutCustomer(tableId);

        //print total for the table and reset it if all orders have been served
        //else, "please wait"
        if(totalAmount < 0){
            System.out.println("please wait");
            return;
        }
        System.out.println(totalAmount);
    }

    @Override
    public void serveOrder(int tableId, int menuId) {
        service.serveReadyOrders(tableId, menuId);
    }

    @Override
    public void receiveOrder(OrderItem orderItem) {
        service.startCookingNextOrder(orderItem);
        //update Tableinfo's orderSent for this table
        service.incrementTableSentOrders(orderItem.tableId);
    }
}
