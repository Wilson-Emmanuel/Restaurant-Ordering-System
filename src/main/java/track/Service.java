package track;

import track.models.MenuItem;
import track.models.OrderItem;
import track.models.TableInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Service {
    /**
     * Menu Item Information
     * With HashMap, we can query any Menu Item info in constant time
     * <menuId, MenuItem>
     */
    private Map<Integer, MenuItem> menuItems = new HashMap<>();//

    /**
     * Orders waiting to be cooked
     * Queue ensures that orders are processed in order or arrival
     */
    private Queue<OrderItem> waitingOrders = new LinkedList<>();

    /**
     * Orders that are currently being cooked
     * Hashmap makes it easy to retrieve list of cooking order with same menuId in constant time
     * The hashmap key is a combination of table and menu information
     * <(menuId,tableId) [List of CookingOrders]>
     */
    private Map<String, Queue<OrderItem>> cookingOrders = new HashMap<>();

    /**
     * Information about tables. This helps us to update table total amount as orders are processed
     * So that we don't have to perform a costly operation when a customer is checking out.
     * <tableId, TableInfo>
     */
    private Map<Integer, TableInfo> tableInfo = new HashMap<>();//

    private int microwaveCount;

    private boolean useTableIdToComputeCookingKey = true;

    //////////////////////////////////////////////////////////
    // PROCESSING
    public void addMenuItem(int menuId, int quantity, int price){
        menuItems.put(menuId, new MenuItem(menuId, quantity, price));
    }

    public void setMicrowaveCount(int microwaveCount){
        this.microwaveCount = microwaveCount;
    }

    public void setUseTableIdToComputeCookingKey(boolean value){
        this.useTableIdToComputeCookingKey = value;
    }


    public boolean isMenuItemAvailable(int menuId, int orderedQuantity){
        return menuItems.containsKey(menuId) && menuItems.get(menuId).quantity >= orderedQuantity;
    }

    public boolean orderMenuItem(int menuId, int orderedQuantity){
        if(!isMenuItemAvailable(menuId, orderedQuantity)) return false;

        menuItems.computeIfPresent(menuId, ( (key, menuItem) -> {
            menuItem.quantity -= orderedQuantity;
            return menuItem;
        }));
        return true;
    }

    public boolean wasScheduledToCook(int menuId, int tableId){
        return cookingOrders.containsKey(getCookingOrderKey(tableId,menuId));
    }

    /**
     * serve cooked order and start cooking another if possible
     * @param menuId, tableId
     * @return: return menuId of the next order to cook or -1 if not possible
     */
    public int serveAndStartCookingNext(int menuId, int tableId){
        String cookingOrderKey = getCookingOrderKey(tableId, menuId);
        cookingOrders.get(cookingOrderKey).poll();
        if(cookingOrders.get(cookingOrderKey).isEmpty())
            cookingOrders.remove(cookingOrderKey);

        microwaveCount++;
        return startCookingNextOrder();
    }

    /**
     * Mark order cooked and return tableId
     * @param menuId
     * @return: Table Id of the just cooked order or -1 if the order was not being cooked
     */
    public int markCookingCompleted(int menuId){
        String cookingOrderKey = getCookingOrderKey(-1, menuId);
        if(!cookingOrders.containsKey(cookingOrderKey))return -1;

        OrderItem completedOrderItem = cookingOrders.get(cookingOrderKey).poll();
        if(cookingOrders.get(cookingOrderKey).isEmpty())
            cookingOrders.remove(cookingOrderKey);
        return completedOrderItem.tableId;
    }

    public void queueOrderToWaitingList(OrderItem orderItem){
        waitingOrders.offer(orderItem);
    }

    /**
     * Check if microwave is available, and start cooking the longest waiting order
     * also decrease the available microwave when currently being used
     * return the menuId of the next order to cook
     * @return
     */
    public int startCookingNextOrder(){
        if(waitingOrders.isEmpty() || microwaveCount <= 0)return -1;

        OrderItem nextOrderToCook = waitingOrders.poll();
        String cookingOrderKey = getCookingOrderKey(nextOrderToCook.tableId,nextOrderToCook.menuId);
        cookingOrders.computeIfAbsent(cookingOrderKey, (k -> new LinkedList<>()))
                .offer(nextOrderToCook);
        microwaveCount--;
        return nextOrderToCook.menuId;
    }

    /**
     * Enqueue the received order unto the list of cooking orders, assuming that there
     * are enough microwave to handle the cooking as the orders arrive
     * @param orderItem
     */
    public void startCookingNextOrder(OrderItem orderItem){
        String cookingOrderKey = getCookingOrderKey(orderItem.tableId,orderItem.menuId);
        cookingOrders.computeIfAbsent(cookingOrderKey, (key -> new LinkedList<>()))
                .offer(orderItem);
    }
    private String getCookingOrderKey(int tableId, int menuId){
        if(!this.useTableIdToComputeCookingKey)tableId = -1;
        return String.format("%d,%d",tableId,menuId);
    }

    public void incrementTableSentOrders(int tableId){
        tableInfo.computeIfAbsent(tableId, (TableInfo::new)).orderSent++;
    }

    public void serveReadyOrders(int tableId, int menuId){
        String readyOrderKey = getCookingOrderKey(tableId, menuId);

        //Remove the order from the cooking order list
        //Menu ID must exist since only ordered menu items are served
        //In case of similar order, remove the one that arrived earliest
        OrderItem readyOrder = cookingOrders.get(readyOrderKey).poll();

        //calculate amount for the order and add it to the order's table total accumulation
        //and increment the order served in the table
        TableInfo readyOrderTable = tableInfo.get(tableId);
        readyOrderTable.totalAmount += menuItems.get(menuId).price;
        readyOrderTable.orderServed++;
    }

    /**
     * checks out the customer at tableId and return the amount to be paid or -1 is customer is not yet done
     * @param tableId
     */
    public long checkoutCustomer(int tableId){
        TableInfo checkTable = tableInfo.get(tableId);

        //print total for the table and reset it if all orders have been served
        //else, "please wait"
        long totalAmount = -1;
        if(checkTable == null || checkTable.orderSent == checkTable.orderServed){
            totalAmount =  checkTable.totalAmount;
            tableInfo.remove(tableId);
        }
        return totalAmount;
    }
}
