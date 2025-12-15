package ctrl;

import java.util.List;
import java.sql.SQLException;
import db.ProductDAOIF;
import model.Product;
import exceptions.DataAccessException; 

public class ProductController {

    private ProductDAOIF productDAO;

    public ProductController(ProductDAOIF productDAO) {
        this.productDAO = productDAO;
    }

    public Product findProductByID(int productID) {
        try {
            return productDAO.findProductByID(productID);
        } catch (SQLException e) {
            throw new DataAccessException( + productID, e);
        }
    }

    public List<Product> findAllProducts() {
        try {
            return productDAO.findAllProducts();
        } catch (SQLException e) {
            throw new DataAccessException(, e);
        }
    }
}
