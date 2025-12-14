package db;

import java.util.List;
import java.sql.SQLException;
import model.Product;

public interface ProductDAOIF {
    Product findProductByID(int productID) throws SQLException;

    List<Product> findAllProducts() throws SQLException;
}
