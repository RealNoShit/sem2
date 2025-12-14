package db;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Service;

public class ServiceDAO implements ServiceDAOIF {

    private final Connection connection;

    public ServiceDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Service findServiceByID(int id) throws SQLException {
        String sql = "SELECT service_id, name, price FROM Service WHERE service_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return buildObject(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Service> findAllServices() throws SQLException {
        String sql = "SELECT service_id, name, price FROM Service";
        List<Service> services = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                services.add(buildObject(rs));
            }
        }
        return services;
    }

    private Service buildObject(ResultSet rs) throws SQLException {
        int id = rs.getInt("service_id");
        String name = rs.getString("name");
        double price = rs.getDouble("price");

        return new Service(id, name, price);
    }
}
