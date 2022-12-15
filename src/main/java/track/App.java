package track;

import track.subsystems.Sub1;
import track.subsystems.Sub2;
import track.subsystems.Sub3;
import track.subsystems.Sub4;

import java.util.Scanner;

public class App {
    static Scanner scanner;
    static Controller controller = null;
    static Service service;
    static Sub1 subsystem1;
    static Sub2 subsystem2;
    static Sub3 subsystem3;
    static Sub4 subsystem4;

    public static void main(String[] args) {

        //create the controller
        controller  = new Controller();

        //create a service for all the subsystems
        service = new Service();

        //create the event listeners
        subsystem1 = new Sub1(service);
        subsystem2 = new Sub2(service);
        subsystem3 = new Sub3(service);
        subsystem4 = new Sub4(service);


        //add listener to the controller

        //subsystem 1
        controller.addOrderPlacedListener(Sub1.SUBSYSTEM_ID, subsystem1);
        //subsystem 2
        controller.addCookingCompletedListener(Sub2.SUBSYSTEM_ID, subsystem2);
        controller.addOrderReceivedListener(Sub2.SUBSYSTEM_ID, subsystem2);
        //subsystem 3
        controller.addCookingCompletedListener(Sub3.SUBSYSTEM_ID, subsystem3);
        controller.addOrderReceivedListener(Sub3.SUBSYSTEM_ID, subsystem3);
        //subsystem 4
        controller.addOrderReceivedListener(Sub4.SUBSYSTEM_ID, subsystem4);
        controller.addOrderReadyListener(Sub4.SUBSYSTEM_ID, subsystem4);
        controller.addCheckoutListener(Sub4.SUBSYSTEM_ID, subsystem4);

        startProcessing();
    }

    private static void startProcessing(){
        scanner = new Scanner(System.in);

        int stepNumber = scanner.nextInt();

        //get the count of types of menu items
        int menuItemCount = scanner.nextInt();

        //If this is step 2, get the count of Microwave
        if(stepNumber == 2){
            service.setMicrowaveCount(scanner.nextInt());
        }

        //pass all menu item info to the controller
        for(int i=0; i<menuItemCount; i++){
            int menuId = scanner.nextInt();
            int initialQuantity = scanner.nextInt();
            int price = scanner.nextInt();

            service.addMenuItem(menuId, initialQuantity, price);
        }

        switch (stepNumber){
            case 1 -> processStep1();
            case 2,3 -> processStep2Or3(stepNumber);
            case 4 -> processStep4();
        }
        scanner.close();
    }

    private static void processStep4() {
        while (scanner.hasNext()) {
            String commandToken = scanner.next().trim();

            if("received".equals(commandToken)){
                scanner.next();//to remove the remaining "order" token in "received order"

                int tableId = scanner.nextInt();
                int menuId = scanner.nextInt();
                controller.receiveOrder(Sub4.SUBSYSTEM_ID, menuId, 1, tableId);
            }
            if("ready".equals(commandToken)){
                int tableId = scanner.nextInt();
                int menuId = scanner.nextInt();
                controller.serveOrder(Sub4.SUBSYSTEM_ID, menuId, tableId);
            }
            if("check".equals(commandToken)){
                int tableId = scanner.nextInt();
                controller.checkout(Sub4.SUBSYSTEM_ID, tableId);
            }
        }
    }


    private static void processStep2Or3(int listenerId) {
        //Notice that in step 2 and 3, although it is necessary for order items to have associated table id
        //such tables id does not affect how orders are cooked. So we need to instruct the Service not to use
        //table id while computing cookingOrder key
        service.setUseTableIdToComputeCookingKey(false);

        while(scanner.hasNext()){
            String commandToken = scanner.next().trim();

            if("received".equals(commandToken)){
                scanner.next();//to remove the remaining "order" in the "received order" token
                int tableId = scanner.nextInt();
                int menuId = scanner.nextInt();
                controller.receiveOrder(listenerId, menuId, 1, tableId);
            }
            if("complete".equals(commandToken)){
                int menuId = scanner.nextInt();
                controller.notifyCookingComplete(listenerId, menuId);
            }
        }
    }

    private static void processStep1() {
        while(scanner.hasNext()){
            String orderString = scanner.next();
            if(!"order".equals(orderString))break;

            int tableId = scanner.nextInt();
            int menuId = scanner.nextInt();
            int quantityOrdered = scanner.nextInt();
            controller.placeOrder(Sub1.SUBSYSTEM_ID, menuId, quantityOrdered, tableId);
        }
    }


}
