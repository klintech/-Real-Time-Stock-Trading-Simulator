package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String id;
    private double cash;
    private Map<String, Integer> holdings;

    public User(String id, double initialCash) {
        this.id = id;
        this.cash = initialCash;
        this.holdings = new HashMap<>();
    }

    public boolean canAfford(double cost) {
        return cash >= cost;
    }

    public boolean hasShares(String symbol, int quantity) {
        return holdings.getOrDefault(symbol, 0) >= quantity;
    }

    public void updateCash(double amount) {
        cash += amount;
    }

    public void addHolding(String symbol, int quantity) {
        holdings.merge(symbol, quantity, Integer::sum);
        if (holdings.get(symbol) == 0) {
            holdings.remove(symbol);
        }
    }

    public String getId() {
        return id;
    }

    public double getCash() {
        return cash;
    }

    public Map<String, Integer> getHoldings() {
        return holdings;
    }

    @Override
    public String toString() {
        return String.format("User: %s, Cash: $%.2f, Holdings: %s", id, cash, holdings);
    }
}