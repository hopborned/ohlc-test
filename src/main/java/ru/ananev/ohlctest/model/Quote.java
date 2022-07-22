package ru.ananev.ohlctest.model;

public interface Quote {

    double getPrice();

    long getInstrumentId();

    long getUtcTimestamp();

}
