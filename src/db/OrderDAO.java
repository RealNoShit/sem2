package dao;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    public Order findOrderByID(int id) throws SQLException {
        String sql = "SELECT * FROM orders WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = buildOrder(rs);
                    loadOrderLines(order);
                    return order;
                }
            }
        }
        return null;
    }

    public void saveOrder(Order order) throws SQLException {
        if (orderExists(order.getOrderID())) {
            updateOrder(order);
        } else {
            insertOrder(order);
        }

        deleteOrderLines(order.getOrderID());
        insertOrderLines(order);
    }

    private boolean orderExists(int id) throws SQLException {
        String sql = "SELECT order_id FROM orders WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void insertOrder(Order order) throws SQLException {
        String sql =
            "INSERT INTO orders (order_id, case_id, ceremony_type, church, ceremony_date, ceremony_time, total_amount, status) " +
            "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderID());
            stmt.setInt(2, order.getCaseRef().getCaseID());
            stmt.setString(3, order.getCeremonyType());
            stmt.setString(4, order.getChurch());
            stmt.setString(5, order.getCeremonyDate());
            stmt.setString(6, order.getCeremonyTime());
            stmt.setDouble(7, order.getTotalAmount());
            stmt.setString(8, order.getStatus());
            stmt.executeUpdate();
        }
    }

    private void updateOrder(Order order) throws SQLException {
        String sql =
            "UPDATE orders SET case_id=?, ceremony_type=?, church=?, ceremony_date=?, ceremony_time=?, total_amount=?, status=? " +
            "WHERE order_id=?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getCaseRef().getCaseID());
            stmt.setString(2, order.getCeremonyType());
            stmt.setString(3, order.getChurch());
            stmt.setString(4, order.getCeremonyDate());
            stmt.setString(5, order.getCeremonyTime());
            stmt.setDouble(6, order.getTotalAmount());
            stmt.setString(7, order.getStatus());
            stmt.setInt(8, order.getOrderID());
            stmt.executeUpdate();
        }
    }

    private void deleteOrderLines(int orderID) throws SQLException {
        String sql = "DELETE FROM orderline WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderID);
            stmt.executeUpdate();
        }
    }

    private void insertOrderLines(Order order) throws SQLException {
        String sql =
            "INSERT INTO orderline (line_id, order_id, product_id, service_id, quantity, unit_price) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        for (OrderLine line : order.getOrderLines()) {
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setInt(1, line.getLineID());
                stmt.setInt(2, order.getOrderID());

                if (line.getProduct() != null) {
                    stmt.setInt(3, line.getProduct().getProductID());
                    stmt.setNull(4, Types.INTEGER);
                }
                else if (line.getService() != null) {
                    stmt.setNull(3, Types.INTEGER);
                    stmt.setInt(4, line.getService().getServiceID());
                }

                stmt.setInt(5, line.getQuantity());
                stmt.setDouble(6, line.getUnitPrice());
                stmt.executeUpdate();
            }
        }
    }

    private Order buildOrder(ResultSet rs) throws SQLException {
        int id = rs.getInt("order_id");
        int caseID = rs.getInt("case_id");

        CaseDAO caseDAO = new CaseDAO(connection);
        Case caseRef = caseDAO.findCaseByID(caseID);

        Order order = new Order(id, caseRef);

        order.setCeremonyDetails(
                rs.getString("ceremony_type"),
                rs.getString("church"),
                rs.getString("ceremony_date"),
                rs.getString("ceremony_time")
        );

        order.setStatus(rs.getString("status"));

        return order;
    }

    private void loadOrderLines(Order order) throws SQLException {
        String sql = "SELECT * FROM orderline WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderID());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int lineID = rs.getInt("line_id");
                    int quantity = rs.getInt("quantity");
                    double price = rs.getDouble("unit_price");

                    Integer productID = (Integer) rs.getObject("product_id");
                    Integer serviceID = (Integer) rs.getObject("service_id");

                    if (productID != null) {
                        ProductDAO productDAO = new ProductDAO(connection);
                        Product p = productDAO.findProductByID(productID);
                        order.addProduct(p, quantity);
                    }
                    else if (serviceID != null) {
                        ServiceDAO serviceDAO = new ServiceDAO(connection);
                        Service s = serviceDAO.findServiceByID(serviceID);
                        order.addService(s);
                    }
                }
            }
        }

        order.calculateTotal();
    }
}
