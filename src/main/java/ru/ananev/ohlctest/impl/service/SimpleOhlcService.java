package ru.ananev.ohlctest.impl.service;

import org.springframework.stereotype.Service;
import ru.ananev.ohlctest.impl.aggregator.AtomicReferenceAggregatorHolder;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.model.OhlcService;
import ru.ananev.ohlctest.model.Quote;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class SimpleOhlcService implements OhlcService {

    private final ConcurrentMap<Long, AtomicReferenceAggregatorHolder> cache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final OhlcStorageService ohlcStorageService;
    private final PeriodService periodService;

    public SimpleOhlcService(ScheduledExecutorService poolExecutor,
                             OhlcStorageService ohlcStorageService,
                             PeriodService periodService) {
        this.scheduler = poolExecutor;
        this.ohlcStorageService = ohlcStorageService;
        this.periodService = periodService;
    }

    @Override
    public Ohlc getCurrent(long instrumentId, OhlcPeriod period) {
        AtomicReferenceAggregatorHolder map = cache.get(instrumentId);
        return map != null ? map.getByPeriod(period) : null;
    }

    @Override
    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return ohlcStorageService.getHistorical(instrumentId, period);
    }

    @Override
    public List<Ohlc> getHistoricalAndCurrent(long instrumentId, OhlcPeriod period) {
        List<Ohlc> historical = ohlcStorageService.getHistorical(instrumentId, period);
        Ohlc current = getCurrent(instrumentId, period);
        if (current == null) {
            return historical;
        } else {
            List<Ohlc> ohlcs = new ArrayList<>(historical.size() + 1);
            ohlcs.add(current);
            ohlcs.addAll(historical);
            return ohlcs;
        }
    }

    @Override
    public void onQuote(Quote quote) {
        cache.computeIfAbsent(quote.getInstrumentId(),
                        id -> new AtomicReferenceAggregatorHolder(ohlcStorageService, scheduler, periodService))
                .addQuote(quote);
    }

}
