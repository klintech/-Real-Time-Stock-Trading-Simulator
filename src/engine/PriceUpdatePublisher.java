package engine;

import model.Stock;

import java.util.ArrayList;
import java.util.List;

public class PriceUpdatePublisher {
    private List<PriceUpdateSubscriber> subscribers = new ArrayList<>();

    public void subscribe(PriceUpdateSubscriber subscriber) {
        subscribers.add(subscriber);
    }

    public void notifySubscribers(Stock stock) {
        subscribers.forEach(subscriber -> subscriber.onPriceUpdate(stock));
    }
}