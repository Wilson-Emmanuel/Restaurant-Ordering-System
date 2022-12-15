package track.subsystems;

import track.Service;
import track.events.CookingCompleted;
import track.events.OrderReceived;
import track.models.OrderItem;

public class Sub3 implements OrderReceived, CookingCompleted {

    private Service service;
    public static final int SUBSYSTEM_ID = 3;

    public Sub3(Service service){
        this.service = service;
    }

    @Override
    public void notifyCookingComplete(int menuId) {
        //completed order is guaranteed to be a valid existing order
        int completedOrderTableId = service.markCookingCompleted(menuId);
        System.out.printf("ready %d %d\n", completedOrderTableId, menuId);
    }

    @Override
    public void receiveOrder(OrderItem orderItem) {
        service.startCookingNextOrder(orderItem);
    }
}
