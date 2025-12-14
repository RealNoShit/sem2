package ctrl;

import java.util.List;
import java.sql.SQLException;
import db.ProductDAOIF;
import model.Product;

public class ProductController {

    private ProductDAOIF productDAO;

    public ProductController(ProductDAOIF productDAO) {
        this.productDAO = productDAO;
    }

    public Product findProductByID(int productID) throws SQLException {
        return productDAO.findProductByID(productID);
    }

    public List<Product> findAllProducts() throws SQLException {
        return productDAO.findAllProducts();
    }
}