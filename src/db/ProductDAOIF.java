package db;

import java.util.List;
import model.Product;

public interface ProductDAOIF {

    Product findProductByID(int id);

    List<Product> findAllProducts();
}
