package track.events;

import track.models.OrderItem;

public interface OrderReceived {
    void receiveOrder(OrderItem orderItem);
}
