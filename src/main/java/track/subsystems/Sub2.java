package track.subsystems;

import track.Service;
import track.events.CookingCompleted;
import track.events.OrderReceived;
import track.models.OrderItem;

public class Sub2 implements OrderReceived, CookingCompleted {

    private Service service;
    public static final int SUBSYSTEM_ID = 2;

    public Sub2(Service service){
        this.service = service;
    }
    @Override
    public void notifyCookingComplete(int menuId) {
        if(!service.wasScheduledToCook(menuId,-1)){
            System.out.println("unexpected input");
            return;
        }

        int nextToCookMenuId = service.serveAndStartCookingNext(menuId,-1);
        String message = nextToCookMenuId == -1? "ok" : String.format("ok %d", nextToCookMenuId);
        System.out.println(message);
    }

    @Override
    public void receiveOrder(OrderItem orderItem) {
        //Enqueue the received order unto the list of orders waiting to be cooked
        service.queueOrderToWaitingList(orderItem);

        int nextToCookMenuId = service.startCookingNextOrder();
        if(nextToCookMenuId > -1){
            System.out.println(nextToCookMenuId);
        }else{
            System.out.println("wait");
        }
    }
}
