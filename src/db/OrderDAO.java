package db;

import model.*;
import java.sql.*;
import java.util.List;

public class OrderDAO {

    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    public Order findOrderByID(int id) {
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
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Failed to find order with id " + id, e);
        }
    }

    public void saveOrder(Order order) {
        try {
            if (orderExists(order.getOrderID())) {
                updateOrder(order);
            } else {
                insertOrder(order);
            }

            deleteOrderLines(order.getOrderID());
            insertOrderLines(order);

        } catch (SQLException e) {
            throw new DataAccessException(
                "Failed to save order with id " + order.getOrderID(), e
            );
        }
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

    private void loadOrderLines(Order order) {
        String sql = "SELECT * FROM orderline WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderID());

            try (ResultSet rs = stmt.executeQuery()) {
                ProductDAO productDAO = new ProductDAO(connection);
                ServiceDAO serviceDAO = new ServiceDAO(connection);

                while (rs.next()) {
                    int quantity = rs.getInt("quantity");

                    Integer productID = (Integer) rs.getObject("product_id");
                    Integer serviceID = (Integer) rs.getObject("service_id");

                    if (productID != null) {
                        Product p = productDAO.findProductByID(productID);
                        order.addProduct(p, quantity);
                    }
                    else if (serviceID != null) {
                        Service s = serviceDAO.findServiceByID(serviceID);
                        order.addService(s);
                    }
                }
            }

            order.calculateTotal();

        } catch (SQLException e) {
            throw new DataAccessException(
                "Failed to load order lines for order " + order.getOrderID(), e
            );
        }
    }
}

