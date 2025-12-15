package db;

import model.Case;
import model.Order;
import model.OrderLine;
import model.Product;
import model.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class OrderDAO {

    private final Connection connection;

    public OrderDAO(Connection connection) {
        this.connection = connection;
    }

    // + createOrder(caseRef : Case) : Order
    public Order createOrder(Case caseReference) {
        if (caseReference == null) {
            throw new IllegalArgumentException("Case reference must not be null.");
        }

        int newOrderId = generateNextOrderId();
        Order order = new Order(newOrderId, caseReference);
        order.setStatus("CREATED");

        save(order);
        return order;
    }

    // + save(order : Order) : void
    public void save(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Order must not be null.");
        }

        try {
            if (orderExists(order.getOrderID())) {
                updateOrderRow(order);
            } else {
                insertOrderRow(order);
            }

            deleteOrderLines(order.getOrderID());
            insertOrderLines(order);

        } catch (SQLException exception) {
            throw new DataAccessException(
                    DBMessages.UPDATE_FAILED + " (order_id=" + order.getOrderID() + ")",
                    exception
            );
        }
    }

    // + buildObject(resultSet : ResultSet) : Order
    public Order buildObject(ResultSet resultSet) throws SQLException {
        int orderId = resultSet.getInt("order_id");
        int caseId = resultSet.getInt("case_id");

        CaseDAO caseDAO = new CaseDAO(connection);
        Case caseReference = caseDAO.findCaseByID(caseId);

        Order order = new Order(orderId, caseReference);

        order.setCeremonyDetails(
                resultSet.getString("ceremony_type"),
                resultSet.getString("church"),
                resultSet.getString("ceremony_date"),
                resultSet.getString("ceremony_time")
        );

        order.setStatus(resultSet.getString("status"));

        loadOrderLines(order);
        order.calculateTotal();

        return order;
    }

    private boolean orderExists(int orderId) throws SQLException {
        String sql = "SELECT order_id FROM orders WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private void insertOrderRow(Order order) throws SQLException {
        String sql =
                "INSERT INTO orders (order_id, case_id, ceremony_type, church, ceremony_date, ceremony_time, total_amount, status) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getOrderID());
            statement.setInt(2, order.getCaseRef().getCaseID());
            statement.setString(3, order.getCeremonyType());
            statement.setString(4, order.getChurch());
            statement.setString(5, order.getCeremonyDate());
            statement.setString(6, order.getCeremonyTime());
            statement.setDouble(7, order.getTotalAmount());
            statement.setString(8, order.getStatus());
            statement.executeUpdate();
        }
    }

    private void updateOrderRow(Order order) throws SQLException {
        String sql =
                "UPDATE orders SET case_id=?, ceremony_type=?, church=?, ceremony_date=?, ceremony_time=?, total_amount=?, status=? " +
                "WHERE order_id=?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getCaseRef().getCaseID());
            statement.setString(2, order.getCeremonyType());
            statement.setString(3, order.getChurch());
            statement.setString(4, order.getCeremonyDate());
            statement.setString(5, order.getCeremonyTime());
            statement.setDouble(6, order.getTotalAmount());
            statement.setString(7, order.getStatus());
            statement.setInt(8, order.getOrderID());
            statement.executeUpdate();
        }
    }

    private void deleteOrderLines(int orderId) throws SQLException {
        String sql = "DELETE FROM orderline WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, orderId);
            statement.executeUpdate();
        }
    }

    private void insertOrderLines(Order order) throws SQLException {
        String sql =
                "INSERT INTO orderline (line_id, order_id, product_id, service_id, quantity, unit_price) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (OrderLine orderLine : order.getOrderLines()) {
                statement.setInt(1, orderLine.getLineID());
                statement.setInt(2, order.getOrderID());

                if (orderLine.getProduct() != null) {
                    statement.setInt(3, orderLine.getProduct().getProductID());
                    statement.setNull(4, Types.INTEGER);
                } else {
                    statement.setNull(3, Types.INTEGER);
                    statement.setInt(4, orderLine.getService().getServiceID());
                }

                statement.setInt(5, orderLine.getQuantity());
                statement.setDouble(6, orderLine.getUnitPrice());
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }

    private void loadOrderLines(Order order) throws SQLException {
        String sql = "SELECT * FROM orderline WHERE order_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, order.getOrderID());

            try (ResultSet resultSet = statement.executeQuery()) {
                ProductDAO productDAO = new ProductDAO(connection);
                ServiceDAO serviceDAO = new ServiceDAO(connection);

                while (resultSet.next()) {
                    int quantity = resultSet.getInt("quantity");
                    Integer productId = (Integer) resultSet.getObject("product_id");
                    Integer serviceId = (Integer) resultSet.getObject("service_id");

                    if (productId != null) {
                        Product product = productDAO.findProductByID(productId);
                        order.addProduct(product, quantity);
                    } else {
                        Service service = serviceDAO.findServiceByID(serviceId);
                        order.addService(service);
                    }
                }
            }
        }
    }

    private int generateNextOrderId() {
        String sql = "SELECT COALESCE(MAX(order_id), 0) + 1 AS next_id FROM orders";

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            resultSet.next();
            return resultSet.getInt("next_id");

        } catch (SQLException exception) {
            throw new DataAccessException(DBMessages.QUERY_FAILED, exception);
        }
    }
}

