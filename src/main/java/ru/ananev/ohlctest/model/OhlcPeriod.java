package ru.ananev.ohlctest.model;

public enum OhlcPeriod {
    /**
     * One minute, starts at 0 second of every minute
     */
    M1(60 * 1000),

    /**
     * One hour, starts at 0:00 of every hour
     */
    H1(60 * 60 * 1000),

    /**
     * One day, starts at 0:00:00 of every day
     */
    D1(24 * 60 * 60 * 1000);

    OhlcPeriod(long millis) {
        this.millis = millis;
    }

    private final long millis;

    public long getMillis() {
        return millis;
    }
}
