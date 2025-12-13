package model;

import java.time.LocalDate;

public class Price {
    private LocalDate date;
    private double price;
    private PriceType priceType;

    public Price(double price) {
        this.date = LocalDate.now();
        this.price = price;
        this.priceType = PriceType.SALE; 
    }

    public double getPrice() {
        return price;
    }

    public LocalDate getDate() {
        return date;
    }

    public PriceType getPriceType() {
        return priceType;
    }
}