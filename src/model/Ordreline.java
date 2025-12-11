public class OrderLine {

    private int lineID;
    private int quantity;
    private double unitPrice;
    private double lineTotal;

    private Product product;
    private Service service;

    
    public OrderLine(int lineID, Product product, int quantity) {
        this.lineID = lineID;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = product.getPrice(); 
        this.lineTotal = calculateLineTotal();
    }

    
    public OrderLine(int lineID, Service service, double unitPrice) {
        this.lineID = lineID;
        this.service = service;
        this.unitPrice = unitPrice;
        this.quantity = 1; 
        this.lineTotal = calculateLineTotal();
    }

    public double calculateLineTotal() {
        return unitPrice * quantity;
    }

    public int getLineID() {
        return lineID;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getLineTotal() {
        return lineTotal;
    }

    public Product getProduct() {
        return product;
    }

    public Service getService() {
        return service;
    }
}
