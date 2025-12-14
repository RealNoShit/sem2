package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Service;

public class ServiceDAO implements ServiceDAOIF {

    private Connection connection;

    public ServiceDAO(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Service findServiceByID(int serviceID) {
        String sql = "SELECT * FROM service WHERE service_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, serviceID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Service(
                    rs.getInt("service_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                );
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException(
                "Could not find service with ID " + serviceID, e
            );
        }
    }

    @Override
    public List<Service> findAllServices() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM service";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                services.add(new Service(
                    rs.getInt("service_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
            return services;

        } catch (SQLException e) {
            throw new DataAccessException(
                "Could not retrieve services", e
            );
        }
    }
}
