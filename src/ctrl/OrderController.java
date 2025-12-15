package ctrl;

import model.Case;
import model.Order;
import model.Product;
import model.Service;
import db.OrderDAO;

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

    public Case openCase(int caseId) {
        return caseController.findCaseByID(caseId);
    }

    public Order registerNewOrder(Case caseReference) {
        if (caseReference == null) {
            throw new IllegalArgumentException("Case must not be null.");
        }
        return orderDAO.createOrder(caseReference);
    }

    public void addCeremonyDetails(Order order,
                                   String ceremonyType,
                                   String church,
                                   String ceremonyDate,
                                   String ceremonyTime) {

        order.setCeremonyDetails(ceremonyType, church, ceremonyDate, ceremonyTime);
        orderDAO.save(order);
    }

    public void addProduct(Order order, int productId, int quantity) {
        Product product = productController.findProductByID(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found.");
        }

        order.addProduct(product, quantity);
        orderDAO.save(order);
    }

    public void addService(Order order, int serviceId) {
        Service service = serviceController.findServiceByID(serviceId);
        if (service == null) {
            throw new IllegalArgumentException("Service not found.");
        }

        order.addService(service);
        orderDAO.save(order);
    }

    public boolean confirmOrder(Order order) {
        order.calculateTotal();
        if (!order.validate()) {
            return false;
        }

        order.setStatus("CONFIRMED");
        orderDAO.save(order);
        return true;
    }
}

