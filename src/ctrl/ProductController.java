package ctrl;

import java.util.List;

import db.ProductDAOIF;
import model.Product;

public class ProductController {

    private final ProductDAOIF productDAO;

    public ProductController(ProductDAOIF productDAO) {
        this.productDAO = productDAO;
    }

    public Product findProductByID(int productID) {
        return productDAO.findProductByID(productID);
    }

    public List<Product> findAllProducts() {
        return productDAO.findAllProducts();
    }
}
