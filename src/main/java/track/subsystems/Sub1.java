package track.subsystems;

import track.Service;
import track.events.OrderPlaced;
import track.models.OrderItem;

public class Sub1 implements OrderPlaced {

    private Service service;
    public static final int SUBSYSTEM_ID = 1;

    public Sub1(Service service){
        this.service = service;
    }

    @Override
    public void acceptOrder(OrderItem orderItem) {

        boolean orderDelivered = service.orderMenuItem(orderItem.menuId, orderItem.quantity);
        if(!orderDelivered){
            System.out.printf("sold out %d\n",orderItem.tableId);
            return;
        }

        int repeatMessage = orderItem.quantity;
        while(repeatMessage-- > 0){
            System.out.printf("received order %d %d\n", orderItem.tableId, orderItem.menuId);
        }
    }
}
