package ui;

import ctrl.CaseController;
import ctrl.OrderController;
import ctrl.ProductController;
import ctrl.ServiceController;

import db.CaseDAO;
import db.CaseDAOIF;
import db.DBConnection;
import db.OrderDAO;
import db.ProductDAO;
import db.ProductDAOIF;
import db.ServiceDAO;
import db.ServiceDAOIF;

import java.sql.Connection;

public class App {
    public static void main(String[] args) {

        Connection connection = DBConnection.getConnection();

        CaseDAOIF caseDAO = new CaseDAO(connection);
        ProductDAOIF productDAO = new ProductDAO(connection);
        ServiceDAOIF serviceDAO = new ServiceDAO(connection);

        OrderDAO orderDAO = new OrderDAO(connection);

        CaseController caseController = new CaseController(caseDAO);
        ProductController productController = new ProductController(productDAO);
        ServiceController serviceController = new ServiceController(serviceDAO);

        OrderController orderController =
                new OrderController(caseController, orderDAO, productController, serviceController);

        OrderUI userInterface = new OrderUI(orderController);
        userInterface.run();
    }
}
