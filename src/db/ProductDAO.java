package db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Product;
import exception.DataAccessException;

public class ProductDAO implements ProductDAOIF {

    private Connection conn;

    public ProductDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Product findProductByID(int id) {
        String sql = "SELECT * FROM product WHERE product_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getDouble("price")
                    );
                }
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                + id,
                e
            );
        }
    }

    @Override
    public List<Product> findAllProducts() {
        String sql = "SELECT * FROM product";
        List<Product> products = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("product_id"),
                    rs.getString("name"),
                    rs.getDouble("price")
                ));
            }
            return products;

        } catch (SQLException e) {
            throw new DataAccessException(
               ,
                e
            );
        }
    }
}
