package track.events;

import track.models.OrderItem;

public interface OrderPlaced {
    void acceptOrder(OrderItem orderItem);
}
