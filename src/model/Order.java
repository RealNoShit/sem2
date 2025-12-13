package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Order {

    private final int orderID;
    private String status;           
    private String ceremonyType;
    private String church;
    private String ceremonyDate;     
    private String ceremonyTime;
    private double totalAmount;
    private final Case caseRef;     
    private final List<OrderLine> orderLines = new ArrayList<>();

    public Order(int orderID, Case caseRef) {
        this.orderID = orderID;
        this.caseRef = Objects.requireNonNull(caseRef, "caseRef must not be null");
        this.status = "Draft";
        this.totalAmount = 0.0;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCeremonyType() {
        return ceremonyType;
    }

    public String getChurch() {
        return church;
    }

    public String getCeremonyDate() {
        return ceremonyDate;
    }

    public String getCeremonyTime() {
        return ceremonyTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Case getCaseRef() {
        return caseRef;
    }

    public List<OrderLine> getOrderLines() {
        return Collections.unmodifiableList(orderLines);
    }

    public void setCeremonyDetails(String type, String church, String date, String time) {
        this.ceremonyType = type;
        this.church = church;
        this.ceremonyDate = date;
        this.ceremonyTime = time;
    }

    public OrderLine addProduct(Product product, int quantity) {
        if (product == null) throw new IllegalArgumentException("product must not be null");
        if (quantity <= 0) throw new IllegalArgumentException("quantity must be > 0");

        double unitPrice = product.getPrice();
        int newLineId = nextLineId();
        OrderLine line = new OrderLine(newLineId, product, quantity);
        orderLines.add(line);
        recalcTotals();
        return line;
    }

    public OrderLine addService(Service service) {
        if (service == null) throw new IllegalArgumentException("service must not be null");

        double unitPrice = service.getPrice();
        int newLineId = nextLineId();
        OrderLine line = new OrderLine(newLineId, service, 1);
        orderLines.add(line);
        recalcTotals();
        return line;
    }

    public boolean removeOrderLine(int lineID) {
        boolean removed = orderLines.removeIf(l -> l.getLineID() == lineID);
        if (removed) recalcTotals();
        return removed;
    }

    public double calculateTotal() {
        recalcTotals();
        return totalAmount;
    }

    public boolean validate() {
        if (orderLines.isEmpty()) return false;
        if (ceremonyType == null || ceremonyType.isEmpty()) return false;
        if (ceremonyDate == null || ceremonyDate.isEmpty()) return false;
        return true;
    }

    private void recalcTotals() {
        double sum = 0.0;
        for (OrderLine l : orderLines) {
            sum += l.calculateLineTotal();
        }
        this.totalAmount = sum;
    }

    private int nextLineId() {
        int max = 0;
        for (OrderLine l : orderLines) {
            if (l.getLineID() > max) max = l.getLineID();
        }
        return max + 1;
    }
}
