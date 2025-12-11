import java.util.List;

public class ProductController {

    private ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public Product findProductByID(int productID) throws Exception {
        return productDAO.findProductByID(productID);
    }

    public List<Product> findAllProducts() throws Exception {
        return productDAO.findAllProducts();
    }
}
