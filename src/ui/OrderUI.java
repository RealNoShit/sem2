package ui;
import ctrl.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class OrderUI {

    private final OrderController orderController;
    private final Scanner scanner;

    private Case currentCase;
    private Order currentOrder;

    /**
     * «create» OrderUI
     */
    public OrderUI(OrderController orderController) {
        this.orderController = orderController;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Simple console loop so you can try things out.
     */
    public void run() {
        boolean running = true;

        while (running) {
            System.out.println();
            System.out.println("=== Order Menu ===");
            System.out.println("1. Open case");
            System.out.println("2. Register new order");
            System.out.println("3. Enter ceremony details");
            System.out.println("4. Add product to order");
            System.out.println("5. Add service to order");
            System.out.println("6. Confirm order");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            int choice = readInt();
            switch (choice) {
                case 1 -> openCase();
                case 2 -> registerNewOrder();
                case 3 -> enterCeremonyDetails();
                case 4 -> addProductToOrder();
                case 5 -> addServiceToOrder();
                case 6 -> confirmOrder();
                case 0 -> running = false;
                default -> System.out.println("Unknown choice");
            }
        }

        System.out.println("Bye.");
    }

    // -------- methods from the UML --------

    public void openCase() {
        System.out.print("Enter case ID: ");
        int caseID = readInt();

        currentCase = orderController.openCase(caseID);
        if (currentCase == null) {
            System.out.println("No case found with ID " + caseID);
        } else {
            System.out.println("Opened case " + currentCase.getCaseID() +
                    " for customer " + currentCase.getCustomerName());
        }
    }

    public void registerNewOrder() {
        if (currentCase == null) {
            System.out.println("You must open a case first.");
            return;
        }

        currentOrder = orderController.registerNewOrder(currentCase.getCaseID());
        System.out.println("New order created with ID " + currentOrder.getOrderID());
    }

    public void enterCeremonyDetails() {
        if (currentOrder == null) {
            System.out.println("You must create an order first.");
            return;
        }

        System.out.print("Ceremony type: ");
        String type = readLine();

        System.out.print("Church: ");
        String church = readLine();

        System.out.print("Date (e.g. 2025-05-01): ");
        String date = readLine();

        System.out.print("Time (e.g. 14:00): ");
        String time = readLine();

        orderController.addCeremonyDetails(currentOrder, type, church, date, time);
        System.out.println("Ceremony details saved.");
    }

    public void addProductToOrder() {
        if (currentOrder == null) {
            System.out.println("You must create an order first.");
            return;
        }

        System.out.print("Product ID: ");
        int productID = readInt();

        System.out.print("Quantity: ");
        int quantity = readInt();

        orderController.addProduct(currentOrder, productID, quantity);
        System.out.println("Product added.");
    }

    public void addServiceToOrder() {
        if (currentOrder == null) {
            System.out.println("You must create an order first.");
            return;
        }

        System.out.print("Service ID: ");
        int serviceID = readInt();

        orderController.addService(currentOrder, serviceID);
        System.out.println("Service added.");
    }

    public void confirmOrder() {
        if (currentOrder == null) {
            System.out.println("You must create an order first.");
            return;
        }

        boolean ok = orderController.confirmOrder(currentOrder);
        if (ok) {
            System.out.println("Order confirmed and saved.");
            currentOrder = null; // finished
        } else {
            System.out.println("Order is not valid – please check details.");
        }
    }

    // -------- helpers --------

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine();   // consume rest of line
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();   // clear invalid input
                System.out.print("Please enter a number: ");
            }
        }
    }

    private String readLine() {
        String line = scanner.nextLine();
        // If the previous nextInt left a newline, read again
        if (line.isEmpty()) {
            line = scanner.nextLine();
        }
        return line;
    }

    // Optional: quick entry point
    public static void main(String[] args) {
        // Wire everything together here once all DAOs/controllers exist
        // (placeholders, adjust to your own classes / constructors)

        try {
            var connection = DBConnection.getConnection();

            CaseDAO caseDAO = new CaseDAO(connection);
            CaseController caseController = new CaseController(caseDAO);

            ProductDAO productDAO = new ProductDAO(connection);
            ProductController productController = new ProductController(productDAO);

            ServiceDAO serviceDAO = new ServiceDAO(connection);
            ServiceController serviceController = new ServiceController(serviceDAO);

            OrderDAO orderDAO = new OrderDAO(connection);

            OrderController orderController =
                    new OrderController(caseController, orderDAO, productController, serviceController);

            new OrderUI(orderController).run();
        } catch (DataAccessException e) {
            System.err.println("Cannot start application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
