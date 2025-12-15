package ctrl;

import model.*;
import db.*;

import java.util.Objects;

public class OrderController {

    private final CaseController caseController;
    private final OrderDAO orderDAO;
    private final ProductController productController;
    private final ServiceController serviceController;

    public OrderController(CaseController caseController,
                           OrderDAO orderDAO,
                           ProductController productController,
                           ServiceController serviceController) {
        this.caseController = Objects.requireNonNull(caseController);
        this.orderDAO = Objects.requireNonNull(orderDAO);
        this.productController = Objects.requireNonNull(productController);
        this.serviceController = Objects.requireNonNull(serviceController);
    }

    public boolean openCase(int caseID) {
        return caseController.findCaseByID(caseID) != null;
    }

    public int registerNewOrder(int caseID) {
        Case c = caseController.findCaseByID(caseID);
        if (c == null) throw new IllegalArgumentException("No case with id " + caseID);

        Order order = orderDAO.createOrder(c);   // creates + persists (or creates and returns with new id)
        return order.getOrderID();
    }

    public void addCeremonyDetails(int orderId, String type, String church, String date, String time) {
        Order order = requireOrder(orderId);
        order.setCeremonyDetails(type, church, date, time);
        orderDAO.update(order); // persist changes
    }

    public void addProduct(int orderId, int productID, int quantity) {
        Order order = requireOrder(orderId);

        Product product = productController.findProductByID(productID);
        if (product == null) throw new IllegalArgumentException("No product with id " + productID);

        order.addProduct(product, quantity);

        orderDAO.update(order);
    }

    public void addService(int orderId, int serviceID) {
        Order order = requireOrder(orderId);

        Service service = serviceController.findServiceByID(serviceID);
        if (service == null) throw new IllegalArgumentException("No service with id " + serviceID);

        order.addService(service);
        orderDAO.update(order);
    }

    public boolean confirmOrder(int orderId) {
        Order order = requireOrder(orderId);

        order.calculateTotal();
        if (!order.validate()) return false;

        orderDAO.confirm(order); // or save/update + mark confirmed
        return true;
    }

    private Order requireOrder(int orderId) {
        Order order = orderDAO.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("No order with id " + orderId);
        }
        return order;
    }
}


