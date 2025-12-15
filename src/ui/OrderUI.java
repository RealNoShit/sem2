package ui;

import ctrl.OrderController;
import model.Case;
import model.Order;

import java.util.InputMismatchException;
import java.util.Scanner;

public class OrderUI {

    private final OrderController orderController;
    private final Scanner scanner;

    private Case currentCase;
    private Order currentOrder;

    public OrderUI(OrderController orderController) {
        this.orderController = orderController;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInteger();

            try {
                switch (choice) {
                    case 1 -> openCase();
                    case 2 -> registerNewOrder();
                    case 3 -> enterCeremonyDetails();
                    case 4 -> addProductToOrder();
                    case 5 -> addServiceToOrder();
                    case 6 -> confirmOrder();
                    case 0 -> running = false;
                    default -> System.out.println("Unknown choice.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Error: " + exception.getMessage());
            }
        }

        System.out.println("Bye.");
    }

    // ------------------------------------------------------------
    // Menu actions
    // ------------------------------------------------------------

    private void openCase() {
        System.out.print("Enter case ID: ");
        int caseId = readInteger();

        Case foundCase = orderController.openCase(caseId);
        if (foundCase == null) {
            System.out.println("No case found with ID " + caseId + ".");
            currentCase = null;
            currentOrder = null;
            return;
        }

        currentCase = foundCase;
        currentOrder = null;
        System.out.println("Case opened.");
    }

    private void registerNewOrder() {
        ensureCaseIsOpen();

        Order newOrder = orderController.registerNewOrder(currentCase);
        currentOrder = newOrder;

        System.out.println("New order created with ID " + currentOrder.getOrderID() + ".");
    }

    private void enterCeremonyDetails() {
        ensureOrderExists();

        System.out.print("Ceremony type: ");
        String ceremonyType = readLine();

        System.out.print("Church: ");
        String church = readLine();

        System.out.print("Date (YYYY-MM-DD): ");
        String ceremonyDate = readLine();

        System.out.print("Time (HH:MM): ");
        String ceremonyTime = readLine();

        orderController.addCeremonyDetails(
                currentOrder,
                ceremonyType,
                church,
                ceremonyDate,
                ceremonyTime
        );

        System.out.println("Ceremony details saved.");
    }

    private void addProductToOrder() {
        ensureOrderExists();

        System.out.print("Product ID: ");
        int productId = readInteger();

        System.out.print("Quantity: ");
        int quantity = readInteger();

        orderController.addProduct(currentOrder, productId, quantity);
        System.out.println("Product added to order.");
    }

    private void addServiceToOrder() {
        ensureOrderExists();

        System.out.print("Service ID: ");
        int serviceId = readInteger();

        orderController.addService(currentOrder, serviceId);
        System.out.println("Service added to order.");
    }

    private void confirmOrder() {
        ensureOrderExists();

        boolean confirmed = orderController.confirmOrder(currentOrder);
        if (!confirmed) {
            System.out.println("Order is not valid. Please check details.");
            return;
        }

        System.out.println("Order confirmed and saved.");
        currentOrder = null;
    }

    // ------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------

    private void ensureCaseIsOpen() {
        if (currentCase == null) {
            throw new IllegalArgumentException("Please open a case first.");
        }
    }

    private void ensureOrderExists() {
        if (currentOrder == null) {
            throw new IllegalArgumentException("Please create an order first.");
        }
    }

    private void printMenu() {
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
    }

    // ------------------------------------------------------------
    // Input handling
    // ------------------------------------------------------------

    private int readInteger() {
        while (true) {
            try {
                int value = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return value;
            } catch (InputMismatchException exception) {
                scanner.nextLine();
                System.out.print("Please enter a number: ");
            }
        }
    }

    private String readLine() {
        String input = scanner.nextLine();
        while (input.isBlank()) {
            input = scanner.nextLine();
        }
        return input;
    }
}


