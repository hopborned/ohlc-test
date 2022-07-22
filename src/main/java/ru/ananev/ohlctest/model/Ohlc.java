package ru.ananev.ohlctest.model;

import java.util.Objects;

public class Ohlc {

    private final long instrumentId;
    private final double openPrice;
    private final double highPrice;
    private final double lowPrice;
    private final double closePrice;
    private final OhlcPeriod ohlcPeriod;
    private final long periodStartUtcTimestamp;

    public Ohlc(long instrumentId, double price, long millis, OhlcPeriod period) {
        this.instrumentId = instrumentId;
        openPrice = closePrice = price;
        lowPrice = highPrice = price;
        ohlcPeriod = period;
        periodStartUtcTimestamp = millis;
    }

    public Ohlc(long instrumentId, double openPrice,
                 double highPrice,
                 double lowPrice,
                 double closePrice,
                 OhlcPeriod ohlcPeriod,
                 long periodStartUtcTimestamp) {
        this.instrumentId = instrumentId;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.ohlcPeriod = ohlcPeriod;
        this.periodStartUtcTimestamp = periodStartUtcTimestamp;
    }

    public long getInstrumentId() {
        return instrumentId;
    }

    public double getOpenPrice() {
        return openPrice;
    }

    public double getHighPrice() {
        return highPrice;
    }

    public double getLowPrice() {
        return lowPrice;
    }

    public double getClosePrice() {
        return closePrice;
    }

    public OhlcPeriod getOhlcPeriod() {
        return ohlcPeriod;
    }

    public long getPeriodStartUtcTimestamp() {
        return periodStartUtcTimestamp;
    }

    public Ohlc update(double price) {
        return new Ohlc(instrumentId, openPrice,
                Double.max(price, highPrice),
                Double.min(price, lowPrice),
                price,
                ohlcPeriod,
                periodStartUtcTimestamp);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ohlc ohlc = (Ohlc) o;
        return instrumentId == ohlc.instrumentId && Double.compare(ohlc.openPrice, openPrice) == 0 && Double.compare(ohlc.highPrice, highPrice) == 0 && Double.compare(ohlc.lowPrice, lowPrice) == 0 && Double.compare(ohlc.closePrice, closePrice) == 0 && periodStartUtcTimestamp == ohlc.periodStartUtcTimestamp && ohlcPeriod == ohlc.ohlcPeriod;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instrumentId, openPrice, highPrice, lowPrice, closePrice, ohlcPeriod, periodStartUtcTimestamp);
    }

    @Override
    public String toString() {
        return "Ohlc{" +
                "instrumentId=" + instrumentId +
                ", openPrice=" + openPrice +
                ", highPrice=" + highPrice +
                ", lowPrice=" + lowPrice +
                ", closePrice=" + closePrice +
                ", ohlcPeriod=" + ohlcPeriod +
                ", periodStartUtcTimestamp=" + periodStartUtcTimestamp +
                '}';
    }
}
