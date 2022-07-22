package ru.ananev.ohlctest.impl.aggregator;

import ru.ananev.ohlctest.impl.service.OhlcStorageService;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.model.Quote;
import ru.ananev.ohlctest.util.CompletedScheduledFuture;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceOhlcAggregator implements OhlcAggregator {

    private final OhlcPeriod ohlcPeriod;
    private final AtomicReference<Ohlc> ohlcRef = new AtomicReference<>();
    private final ScheduledExecutorService scheduler;
    private ScheduledFuture<?> scheduledFuture = new CompletedScheduledFuture<>(null);
    private final OhlcStorageService ohlcStorageService;
    private final PeriodService periodService;

    public AtomicReferenceOhlcAggregator(OhlcPeriod ohlcPeriod,
                                         ScheduledExecutorService scheduler,
                                         OhlcStorageService ohlcStorageService, PeriodService periodService) {
        this.ohlcPeriod = ohlcPeriod;
        this.scheduler = scheduler;
        this.ohlcStorageService = ohlcStorageService;
        this.periodService = periodService;
    }

    public Ohlc getCurrent() {
        return ohlcRef.get();
    }

    public void update(Quote quote) {
        //we can check if quote timestamp exceeds the max delay
        long quotePeriodStart = periodService.periodStart(quote.getUtcTimestamp(), ohlcPeriod);
        Ohlc prev = ohlcRef.getAndUpdate(ohlc -> {
            if (ohlc == null) {
                return new Ohlc(quote.getInstrumentId(), quote.getPrice(), quotePeriodStart, ohlcPeriod);
            } else {
                if (ohlc.getPeriodStartUtcTimestamp() != quotePeriodStart) {
                    return new Ohlc(quote.getInstrumentId(), quote.getPrice(), quotePeriodStart, ohlcPeriod);
                } else {
                    return ohlc.update(quote.getPrice());
                }
            }
        });
        if (prev == null) {
            scheduleReset(quotePeriodStart, quote.getUtcTimestamp());
        } else {
            if (prev.getPeriodStartUtcTimestamp() != quotePeriodStart) {
                ohlcStorageService.store(prev);
                scheduleReset(quotePeriodStart, quote.getUtcTimestamp());
            }
        }
    }


    private void scheduleReset(long ohlcPeriodStart, long quoteTimestamp) {
        scheduledFuture.cancel(false);
        scheduledFuture = scheduler.schedule(() -> {
            //do update if period didn't change
            Ohlc last = ohlcRef.getAndUpdate(ohlc -> shouldReset(ohlcPeriodStart, ohlc) ? null : ohlc);
            //check if updated
            if (shouldReset(ohlcPeriodStart, last)) {
                ohlcStorageService.store(last);
            }
        }, periodService.millisToExpire(quoteTimestamp, ohlcPeriod), TimeUnit.MILLISECONDS);
    }

    private static boolean shouldReset(long ohlcPeriodStart, Ohlc last) {
        return last != null && last.getPeriodStartUtcTimestamp() == ohlcPeriodStart;
    }


}
