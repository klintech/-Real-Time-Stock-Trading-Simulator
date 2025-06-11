package util;

import model.Trade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {
    public static void generateReport(String userId) {
        List<Trade> trades = TradePersister.loadTrades();
        List<Trade> userTrades = trades.stream()
                .filter(t -> t.getBuyerId().equals(userId) || t.getSellerId().equals(userId))
                .collect(Collectors.toList());

        System.out.println("\nTrade Report for " + userId);
        System.out.println("Total Trades: " + userTrades.size());

        Map<String, Double> profitLoss = userTrades.stream()
                .collect(Collectors.groupingBy(
                        Trade::getStockSymbol,
                        Collectors.summingDouble(t ->
                                t.getBuyerId().equals(userId) ? -t.getQuantity() * t.getPrice() :
                                        t.getQuantity() * t.getPrice()
                        )));

        System.out.println("Profit/Loss by Stock:");
        profitLoss.forEach((symbol, pl) ->
                System.out.printf("%s: $%.2f%n", symbol, pl));

        System.out.println("\nRecent Trades:");
        userTrades.forEach(System.out::println);
    }
}