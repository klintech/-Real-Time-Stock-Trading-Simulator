package util;

import model.Trade;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TradePersister {
    private static final String FILE_NAME = "trades.dat";

    public static void saveTrade(Trade trade) {
        List<Trade> trades = loadTrades();
        trades.add(trade);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(trades);
        } catch (IOException e) {
            System.err.println("Error saving trade: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Trade> loadTrades() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Trade>) ois.readObject();
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading trades: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}