import java.util.List;

public interface IProductDAOF {
    Product findProductByID(int productID) throws Exception;

    List<Product> findAllProducts() throws Exception;
}
