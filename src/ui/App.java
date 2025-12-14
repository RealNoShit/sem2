package ui;
import ctrl.*;
import db.*;

import java.sql.Connection;

public class App {
    public static void main(String[] args) {
        // Data layer
        Connection connection = DBConnection.getConnection();

        CaseDAOIF caseDAO = new CaseDAO(connection);
        ProductDAOIF productDAO = new ProductDAO(connection);
        ServiceDAOIF serviceDAO = new ServiceDAO(connection);
        
        OrderDAO orderDAO = new OrderDAO(connection, caseDAO, productDAO, serviceDAO);

        // Controller layer
        CaseController caseController = new CaseController(caseDAO);
        ProductController productController = new ProductController(productDAO);
        ServiceController serviceController = new ServiceController(serviceDAO);

        OrderController orderController =
                new OrderController(caseController, orderDAO, productController, serviceController);

        // Presentation layer
        OrderUI ui = new OrderUI(orderController);
        ui.run();
    }
}
