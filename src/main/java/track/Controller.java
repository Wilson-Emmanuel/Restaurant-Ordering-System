package track;

import track.events.*;
import track.models.MenuItem;
import track.models.OrderItem;
import track.models.TableInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Controller {

    //Listeners for OrderPlaced event. <ListenerID, Listener>
    private Map<Integer, OrderPlaced> orderPlacedListeners = new HashMap<>();

    private Map<Integer, OrderReady> orderReadyListeners = new HashMap<>();
    private Map<Integer, OrderReceived> orderReceivedListeners = new HashMap<>();
    private Map<Integer, CookingCompleted> cookingCompletedListeners = new HashMap<>();
    private Map<Integer, Checkout> checkoutListeners = new HashMap<>();

    //////////////////////////////////////////////////////
    // REGISTER AND REMOVE LISTENER
    //Order Placed
    public void addOrderPlacedListener(int listenerId, OrderPlaced listener){
        orderPlacedListeners.put(listenerId, listener);
    }
    public OrderPlaced removedOrderPlacedListener(int listenerId){
        return orderPlacedListeners.remove(listenerId);
    }
    //Order Ready
    public void addOrderReadyListener(int listenerId, OrderReady listener){
        orderReadyListeners.put(listenerId, listener);
    }
    public OrderReady removedOrderReadyListener(int listenerId){
        return orderReadyListeners.remove(listenerId);
    }
    //Order Received
    public void addOrderReceivedListener(int listenerId, OrderReceived listener){
        orderReceivedListeners.put(listenerId, listener);
    }
    public OrderReceived removedOrderReceivedListener(int listenerId){
        return orderReceivedListeners.remove(listenerId);
    }
    //Cooking Completed
    public void addCookingCompletedListener(int listenerId, CookingCompleted listener){
        cookingCompletedListeners.put(listenerId, listener);
    }
    public CookingCompleted removedCookingCompletedListener(int listenerId){
        return cookingCompletedListeners.remove(listenerId);
    }
    //Checkout
    public void addCheckoutListener(int listenerId, Checkout listener){
        checkoutListeners.put(listenerId, listener);
    }
    public Checkout removeCheckoutListener(int listenerId){
        return checkoutListeners.remove(listenerId);
    }


    // TRIGGER EVEN LISTENERS
    public void placeOrder(int listenerId, int menuId, int quantity, int tableId){
        //For broadcast event, all listeners will be notified instead of a particular one
        if(!orderPlacedListeners.containsKey(listenerId))
            return;
        orderPlacedListeners.get(listenerId).acceptOrder(new OrderItem(menuId, quantity, tableId));
    }

    public void receiveOrder(int listenerId, int menuId, int quantity, int tableId){
        if(!orderReceivedListeners.containsKey(listenerId))
            return;
        orderReceivedListeners.get(listenerId).receiveOrder(new OrderItem(menuId, quantity, tableId));
    }

    public void serveOrder(int listenerId, int menuId, int tableId){
        if(!orderReadyListeners.containsKey(listenerId))
            return;
        orderReadyListeners.get(listenerId).serveOrder(tableId, menuId);
    }
    public void notifyCookingComplete(int listenerId, int menuId){
        if(!cookingCompletedListeners.containsKey(listenerId))
            return;
        cookingCompletedListeners.get(listenerId).notifyCookingComplete(menuId);
    }
    public void checkout(int listenerId, int tableId){
        if(!checkoutListeners.containsKey(listenerId))
            return;
        checkoutListeners.get(listenerId).checkOut(tableId);
    }



}
