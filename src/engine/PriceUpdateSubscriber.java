package engine;

import model.Stock;

public interface PriceUpdateSubscriber {
    void onPriceUpdate(Stock stock);
}