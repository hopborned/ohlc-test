package ru.ananev.ohlctest.impl.aggregator;

import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.Quote;

public interface OhlcAggregator {
    Ohlc getCurrent();

    void update(Quote quote);
}
