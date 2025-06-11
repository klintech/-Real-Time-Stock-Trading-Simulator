package model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Trade implements Serializable {
    private String id;
    private String buyerId;
    private String sellerId;
    private String stockSymbol;
    private int quantity;
    private double price;
    private LocalDateTime timestamp;

    public Trade(String buyerId, String sellerId, String stockSymbol, int quantity, double price) {
        this.id = java.util.UUID.randomUUID().toString();
        this.buyerId = buyerId;
        this.sellerId = sellerId;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
        this.price = price;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("Trade: %s bought %d shares of %s from %s at $%.2f on %s",
                buyerId, quantity, stockSymbol, sellerId, price, timestamp);
    }
}