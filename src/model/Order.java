package model;

import java.io.Serializable;

public class Order implements Serializable, Comparable<Order> {
    private String id;
    private String userId;
    private String stockSymbol;
    private Type type;
    private int quantity;
    private double price;

    public enum Type { BUY, SELL }

    public Order(String userId, String stockSymbol, Type type, int quantity, double price) {
        this.id = stockSymbol + "-" + java.util.UUID.randomUUID().toString();
        this.userId = userId;
        this.stockSymbol = stockSymbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public Type getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public int compareTo(Order other) {
        if (this.type == Type.BUY) {
            return Double.compare(other.price, this.price); // Highest price first
        } else {
            return Double.compare(this.price, other.price); // Lowest price first
        }
    }

    @Override
    public String toString() {
        return String.format("Order %s: %s %s %d shares of %s at $%.2f", id, userId, type, quantity, stockSymbol, price);
    }
}