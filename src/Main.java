import engine.MarketEngine;
import model.Order;
import util.ReportGenerator;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        MarketEngine engine = new MarketEngine();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Stock Trading Simulator");
        System.out.println("Commands: buy <symbol> <quantity> <price>, sell <symbol> <quantity> <price>, report, stocks, portfolio, cancel <orderId>, updateprice <symbol> <price>, batchupdate <symbol> <changes>, quit");

        while (true) {
            System.out.println("\nAvailable Stocks:");
            engine.getStocks().forEach((symbol, stock) -> System.out.println(stock));
            System.out.print("> ");
            String input = scanner.nextLine().trim().toLowerCase();
            String[] parts = input.split("\\s+");

            if (parts[0].equals("quit")) {
                engine.shutdown();
                break;
            } else if (parts[0].equals("buy") && parts.length == 4) {
                engine.placeOrder(new Order("User1", parts[1].toUpperCase(), Order.Type.BUY,
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3])));
            } else if (parts[0].equals("sell") && parts.length == 4) {
                engine.placeOrder(new Order("User2", parts[1].toUpperCase(), Order.Type.SELL,
                        Integer.parseInt(parts[2]), Double.parseDouble(parts[3])));
            } else if (parts[0].equals("report")) {
                ReportGenerator.generateReport("User1");
            } else if (parts[0].equals("stocks")) {
                engine.getStocks().forEach((symbol, stock) -> System.out.println(stock));
            } else if (parts[0].equals("portfolio")) {
                System.out.println(engine.getUsers().get("User1"));
            } else if (parts[0].equals("cancel") && parts.length == 2) {
                engine.cancelOrder(parts[1], parts[1].split("-")[0].toUpperCase());
            } else if (parts[0].equals("updateprice") && parts.length == 3) {
                engine.updateStockPrice(parts[1].toUpperCase(), Double.parseDouble(parts[2]));
            } else if (parts[0].equals("batchupdate") && parts.length == 3) {
                engine.batchUpdateStockPrice(parts[1].toUpperCase(), Integer.parseInt(parts[2]));
            } else {
                System.out.println("Invalid command. Try: buy <symbol> <quantity> <price>, sell <symbol> <quantity> <price>, report, stocks, portfolio, cancel <orderId>, updateprice <symbol> <price>, batchupdate <symbol> <changes>, quit");
            }
        }
        scanner.close();
    }
}