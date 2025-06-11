package engine;

import model.Order;
import model.Stock;
import model.Trade;
import model.User; // New import for portfolio management
import util.TradePersister;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;

public class MarketEngine implements PriceUpdateSubscriber {
    private Map<String, Stock> stocks;
    private Map<String, PriorityBlockingQueue<Order>> buyOrders;
    private Map<String, PriorityBlockingQueue<Order>> sellOrders;
    private Map<String, User> users; // New field for portfolio management
    private PriceUpdatePublisher publisher;
    private ExecutorService executor;
    private boolean running;

    public MarketEngine() {
        this.stocks = new HashMap<>();
        this.stocks.put("AAPL", new Stock("AAPL", 150.00));
        this.stocks.put("GOOGL", new Stock("GOOGL", 2800.00));
        this.buyOrders = new HashMap<>();
        this.sellOrders = new HashMap<>();
        this.users = new HashMap<>(); // Initialize users
        this.users.put("User1", new User("User1", 10000.0)); // Starting cash: $10,000
        this.users.put("User2", new User("User2", 10000.0));
        this.publisher = new PriceUpdatePublisher();
        this.executor = Executors.newFixedThreadPool(4);
        this.running = true;

        stocks.keySet().forEach(symbol -> {
            buyOrders.put(symbol, new PriorityBlockingQueue<>());
            sellOrders.put(symbol, new PriorityBlockingQueue<>());
        });

        publisher.subscribe(this);
        startMarketSimulation();
    }

    public void placeOrder(Order order) {
        executor.submit(() -> {
            String symbol = order.getStockSymbol();
            User user = users.get(order.getUserId());
            if (!stocks.containsKey(symbol)) {
                System.out.println("Order rejected: Invalid symbol " + symbol);
                return;
            }
            if (order.getType() == Order.Type.BUY) {
                double cost = order.getQuantity() * order.getPrice();
                if (!user.canAfford(cost)) {
                    System.out.println("Order rejected: Insufficient funds for " + order);
                    return;
                }
            } else {
                if (!user.hasShares(symbol, order.getQuantity())) {
                    System.out.println("Order rejected: Insufficient shares for " + order);
                    return;
                }
            }
            PriorityBlockingQueue<Order> queue = order.getType() == Order.Type.BUY ?
                    buyOrders.get(symbol) : sellOrders.get(symbol);
            queue.add(order);
            matchOrders(symbol);
            System.out.println("Order placed: " + order);
        });
    }

    private void matchOrders(String symbol) {
        PriorityBlockingQueue<Order> buys = buyOrders.get(symbol);
        PriorityBlockingQueue<Order> sells = sellOrders.get(symbol);

        synchronized (buys) {
            synchronized (sells) {
                while (!buys.isEmpty() && !sells.isEmpty()) {
                    Order buy = buys.peek();
                    Order sell = sells.peek();

                    if (buy.getPrice() >= sell.getPrice()) {
                        int quantity = Math.min(buy.getQuantity(), sell.getQuantity());
                        double price = (buy.getPrice() + sell.getPrice()) / 2.0;

                        // Apply price impact based on trade volume
                        double priceImpact = quantity * 0.01; // 1 cent per share traded
                        price += priceImpact; // Increase price for large buys

                        Trade trade = new Trade(buy.getUserId(), sell.getUserId(), symbol, quantity, price);
                        TradePersister.saveTrade(trade);

                        // Update user portfolios
                        User buyer = users.get(buy.getUserId());
                        User seller = users.get(sell.getUserId());
                        buyer.updateCash(-quantity * price);
                        buyer.addHolding(symbol, quantity);
                        seller.updateCash(quantity * price);
                        seller.addHolding(symbol, -quantity);

                        stocks.get(symbol).setPrice(price);
                        publisher.notifySubscribers(stocks.get(symbol));

                        buys.poll();
                        sells.poll();

                        if (buy.getQuantity() > quantity) {
                            buys.add(new Order(buy.getUserId(), symbol, Order.Type.BUY, buy.getQuantity() - quantity, buy.getPrice()));
                        }
                        if (sell.getQuantity() > quantity) {
                            sells.add(new Order(sell.getUserId(), symbol, Order.Type.SELL, sell.getQuantity() - quantity, sell.getPrice()));
                        }
                    } else {
                        break;
                    }
                }
            }
        }
    }

    private void startMarketSimulation() {
        executor.submit(() -> {
            Random random = new Random();
            while (running) {
                try {
                    Thread.sleep(random.nextInt(5000) + 10000); // 10-15 seconds
                    stocks.forEach((symbol, stock) -> {
                        double change = (random.nextDouble() - 0.5) * 10;
                        stock.setPrice(stock.getPrice() + change);
                        publisher.notifySubscribers(stock);
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void updateStockPrice(String symbol, double newPrice) {
        if (stocks.containsKey(symbol)) {
            Stock stock = stocks.get(symbol);
            stock.setPrice(newPrice);
            publisher.notifySubscribers(stock);
            System.out.println("Price updated for " + symbol + " to $" + String.format("%.2f", newPrice));
        } else {
            System.out.println("Invalid stock symbol: " + symbol);
        }
    }

    // New method for batch price updates
    public void batchUpdateStockPrice(String symbol, int changes) {
        if (stocks.containsKey(symbol)) {
            Stock stock = stocks.get(symbol);
            Random random = new Random();
            for (int i = 0; i < changes; i++) {
                double change = (random.nextDouble() - 0.5) * 10;
                stock.setPrice(stock.getPrice() + change);
                publisher.notifySubscribers(stock);
                System.out.println("Batch update " + (i + 1) + "/" + changes + " for " + symbol + ": $" + String.format("%.2f", stock.getPrice()));
                try {
                    Thread.sleep(1000); // 1-second delay between updates
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        } else {
            System.out.println("Invalid stock symbol: " + symbol);
        }
    }

    // New method for order cancellation
    public void cancelOrder(String orderId, String symbol) {
        if (!buyOrders.containsKey(symbol) || !sellOrders.containsKey(symbol)) {
            System.out.println("Invalid symbol: " + symbol);
            return;
        }
        synchronized (buyOrders.get(symbol)) {
            if (buyOrders.get(symbol).removeIf(order -> order.getId().equals(orderId))) {
                System.out.println("Buy order " + orderId + " canceled.");
                return;
            }
        }
        synchronized (sellOrders.get(symbol)) {
            if (sellOrders.get(symbol).removeIf(order -> order.getId().equals(orderId))) {
                System.out.println("Sell order " + orderId + " canceled.");
                return;
            }
        }
        System.out.println("Order " + orderId + " not found.");
    }

    public Map<String, Stock> getStocks() {
        return stocks;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    @Override
    public void onPriceUpdate(Stock stock) {
        System.out.println("Price Update: " + stock);
    }

    public void shutdown() {
        running = false;
        executor.shutdown();
    }
}