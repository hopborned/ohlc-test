package ru.ananev.ohlctest.impl.aggregator;

import ru.ananev.ohlctest.impl.service.OhlcStorageService;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.model.Quote;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;

public class AtomicReferenceAggregatorHolder {

    private static final OhlcPeriod[] PERIODS = OhlcPeriod.values();
    private final Map<OhlcPeriod, OhlcAggregator> aggregates;

    public AtomicReferenceAggregatorHolder(OhlcStorageService storageService, ScheduledExecutorService scheduler, PeriodService periodService) {
        this.aggregates = createAggregates(scheduler, storageService, periodService);
    }

    public Ohlc getByPeriod(OhlcPeriod period) {
        return aggregates.get(period).getCurrent();
    }

    public void addQuote(Quote quote) {
        for (OhlcPeriod period : PERIODS) {
            final OhlcAggregator aggregator = aggregates.get(period);
            aggregator.update(quote);
        }
    }

    private static Map<OhlcPeriod, OhlcAggregator> createAggregates(ScheduledExecutorService scheduler,
                                                                    OhlcStorageService storageService,
                                                                    PeriodService periodService) {
        EnumMap<OhlcPeriod, OhlcAggregator> enumMap = new EnumMap<>(OhlcPeriod.class);
        for (OhlcPeriod period : PERIODS) {
            enumMap.put(period, new AtomicReferenceOhlcAggregator(period, scheduler, storageService, periodService));
        }
        return Collections.unmodifiableMap(enumMap);
    }

}
