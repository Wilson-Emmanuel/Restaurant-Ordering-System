
## Solution Pattern
I used `Observer` design pattern to solve this problem. From the problem statement, the sub systems listen to events and ,in turns, perform some actions. The events are triggered by external elements like workers or even the subsystems themselves. However, in this problem, events are triggered by parsing the `std` inputs. Below are several types of events that subsystems listen to.
- `OrderPlaced` event: subsystem 1 checks if the order can be placed or not. if yes, it publishes 'OrderReceived' event so that other subsystem would know that an order has been accepted.
- `OrderReceived` event: subsystem 2 receives this event published by subsystem 1 and notifies a staff. Once the staff is done cooking an order, he publishes `CookingCompleted` event. 
- `CookingCompleted`: subsystem 2 also receives this event published by the cooking staff. 
- `OrderReady` event: subsystem 4 receives this event from the cooking staff after an order is ready. this even differs from `CookingCompleted` event because `CookingCompleted` event does not contain the table where the order was placed.
- `Checkout` event: subsystem 4 also receives this event when a customer clicks on the checkout button, it then calculates the total amount of orders served to the table or instructs the customer to wait if all orders have not been served. 
Note that are the subsystems are not actually publishing any events. They are just printing the events to the console. Event publishing is simulated via input data from `std` stream.

## Components
- `Controller`: This is like the restaurant's central system that integrates and coordinates other components. It creates `event listeners` and properly calls the listeners when events happen. Events are triggered when the application receives commands. In this particular problem, commands are sent to the service from the `std` input stream. This service also has the feature of sending events to a particular subscriber/listener as opposed to broadcasting the events to all subscribers. This is due to the requirements from the `problem statement`.
- `Sub1`, `Sub2`, `Sub3`, `Sub4`. These are the 4 subsystems that listen to various event. <b> In this particular problem, subsystems prints out their events. This solution can easily been extended to allow subsystems to also publish events to the `Controller` which are, in turn, listened by other subsystems or even same subsystem.</b>
- `Service`: This component provides common services to all subsystems. Only one instance of the Service component is injected into every subsystem on startup. It also holds all the data for the running application in the memory, hence its onetime instantiation to avoid losing the data. Another better approach could be splitting the service into several smaller services for each subsystem and/or using `Dependency Injection` to handle injecting services into subsystems at runtime with external or file-based datasource.

## Performance related decisions
- `Hashmap` is used to store menu item information. This makes it possible to query information about any menu item in constant time.
- Orders that are waiting to be cooked are stored in a `Queue`. This makes it easy to retrieve/process orders that arrive earlier in constant time. Orders are removed from the waiting list once they start cooking.
- Orders that are currently being cooked are stored in a `HashMap` (whose key is a combination of the menuId and tableId). The keys map a `Queue` of cooking orders with same menuId and tableID combination. The reason for combining menuId and tableId to create key is mainly because of requirements in Step 4 where tableid is necessary identify the order that Orders arrives for cooking.
- Instead of having to calculate table total amount when a customer requests for a checkout, which could be costly and/or error-prone especially when a lot of orders have been made on the table, I choose to compute table information on the fly while orders are being placed and served. Necessary table information includes `count of orders sent`, `count of orders served`, and the `total amount of orders served`.


## Further Points
Although not yet implemented, this solution can easily be extended to solve more complexity situation that closely simulates the real-world scenario by using `Concurrency programming`, which works well when there is a constant stream of events.
This solution could easily be changed to use `Publish-Subscribe` (Pub-Sub) pattern, which is a close concept to `Observer` pattern that I used. The difference is that in the `Observer` pattern, there is a bit of tight coupling between the Observer (subsystems in this case) and the Subject (the Controller). Whereas, in `Pub-Sub` the event listeners (called the Subscribers) are connected to an `Event Hub` (e.g. message broker). The Producer can constantly produce and send events to the message broker while the Subscribers have no knowledge about the producer of the events.