public class Product {
    private int productID;
    private String name;
    private double price;

    public Product(int productID, String name, double price) {
        this.productID = productID;
        this.name = name;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public int getProductID() {
        return productID;
    }

    public String getName() {
        return name;
    }
}
