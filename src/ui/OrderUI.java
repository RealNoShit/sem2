package ui;
import ctrl.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class OrderUI {

    private final OrderController orderController;
    private final Scanner scanner = new Scanner(System.in);

    private Integer currentCaseId = null;
    private Integer currentOrderId = null;

    public OrderUI(OrderController orderController) {
        this.orderController = orderController;
    }

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

    public void openCase() {
        System.out.print("Enter case ID: ");
        int caseId = readInt();

        boolean ok = orderController.openCase(caseId);
        if (!ok) {
            System.out.println("No case found with ID " + caseId);
            currentCaseId = null;
            currentOrderId = null;
        } else {
            currentCaseId = caseId;
            currentOrderId = null;
            System.out.println("Case opened.");
        }
    }

    public void registerNewOrder() {
        if (currentCaseId == null) {
            System.out.println("Open a case first.");
            return;
        }

        int orderId = orderController.registerNewOrder(currentCaseId);
        currentOrderId = orderId;
        System.out.println("New order created with ID " + orderId);
    }

    public void enterCeremonyDetails() {
        if (currentOrderId == null) {
            System.out.println("Create an order first.");
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

        orderController.addCeremonyDetails(currentOrderId, type, church, date, time);
        System.out.println("Ceremony details saved.");
    }

    public void addProductToOrder() {
        if (currentOrderId == null) {
            System.out.println("Create an order first.");
            return;
        }

        System.out.print("Product ID: ");
        int productId = readInt();

        System.out.print("Quantity: ");
        int qty = readInt();

        orderController.addProduct(currentOrderId, productId, qty);
        System.out.println("Product added.");
    }

    public void addServiceToOrder() {
        if (currentOrderId == null) {
            System.out.println("Create an order first.");
            return;
        }

        System.out.print("Service ID: ");
        int serviceId = readInt();

        orderController.addService(currentOrderId, serviceId);
        System.out.println("Service added.");
    }

    public void confirmOrder() {
        if (currentOrderId == null) {
            System.out.println("Create an order first.");
            return;
        }

        boolean ok = orderController.confirmOrder(currentOrderId);
        if (ok) {
            System.out.println("Order confirmed and saved.");
            currentOrderId = null;
        } else {
            System.out.println("Order is not valid – check details.");
        }
    }

    // ---------- input helpers ----------

    private int readInt() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return value;
            } catch (InputMismatchException e) {
                scanner.nextLine();
                System.out.print("Please enter a number: ");
            }
        }
    }

    private String readLine() {
        String line = scanner.nextLine();
        if (line.isEmpty()) line = scanner.nextLine();
        return line;
    }
}

