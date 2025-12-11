import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO implements IProductDAOF {

    private Connection conn;

    public ProductDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Product findProductByID(int id) throws Exception {
        String sql = "SELECT * FROM product WHERE product_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getDouble("price")
            );
        }
        return null;
    }

    @Override
    public List<Product> findAllProducts() throws Exception {
        String sql = "SELECT * FROM product";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        List<Product> products = new ArrayList<>();

        while (rs.next()) {
            products.add(new Product(
                rs.getInt("product_id"),
                rs.getString("name"),
                rs.getDouble("price")
            ));
        }
        return products;
    }
}

