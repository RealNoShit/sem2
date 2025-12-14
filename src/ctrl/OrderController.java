package ctrl;
import model.*;
import db.*;

import java.util.Objects;

public class OrderController {

    private final CaseController caseController;
    private final OrderDAO orderDAO;
    private final ProductController productController;
    private final ServiceController serviceController;

    /**
     * Creates an OrderController.
     */
    public OrderController(CaseController caseController,
                           OrderDAO orderDAO,
                           ProductController productController,
                           ServiceController serviceController) {
        this.caseController = Objects.requireNonNull(caseController, "caseController");
        this.orderDAO = Objects.requireNonNull(orderDAO, "orderDAO");
        this.productController = Objects.requireNonNull(productController, "productController");
        this.serviceController = Objects.requireNonNull(serviceController, "serviceController");
    }

    /**
     * Opens an existing case.
     */
    public Case openCase(int caseID) {
        return caseController.findCaseByID(caseID);
    }

    /**
     * Registers a new order for the given case.
     */
    public Order registerNewOrder(int caseID) {
        Case caseRef = openCase(caseID);
        if (caseRef == null) {
            throw new IllegalArgumentException("No case found with id " + caseID);
        }
        return orderDAO.createOrder(caseRef);
    }

    /**
     * Adds / updates ceremony details on the given order.
     */
    public void addCeremonyDetails(Order order,
                                   String type,
                                   String church,
                                   String date,
                                   String time) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }
        order.setCeremonyDetails(type, church, date, time);
    }

    /**
     * Adds a product line to the order.
     */
    public void addProduct(Order order, int productID, int quantity) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }
        Product product = productController.findProductByID(productID);
        if (product == null) {
            throw new IllegalArgumentException("No product found with id " + productID);
        }
        order.addProduct(product, quantity);
    }

    /**
     * Adds a service line to the order.
     */
    public void addService(Order order, int serviceID) {
        if (order == null) {
            throw new IllegalArgumentException("order cannot be null");
        }
        Service service = serviceController.findServiceByID(serviceID);
        if (service == null) {
            throw new IllegalArgumentException("No service found with id " + serviceID);
        }
        order.addService(service);
    }

    /**
     * Validates the order and, if valid, saves it via the DAO.
     *
     * @return true if the order validated and was saved, false otherwise.
     */
    public boolean confirmOrder(Order order) {
        if (order == null) {
            return false;
        }

        // Make sure totals are up to date before validating / persisting
        order.calculateTotal();

        if (!order.validate()) {
            return false;
        }

        orderDAO.save(order);
        return true;
    }
}
