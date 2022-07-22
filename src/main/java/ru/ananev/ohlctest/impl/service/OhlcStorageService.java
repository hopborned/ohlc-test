package ru.ananev.ohlctest.impl.service;

import org.springframework.stereotype.Service;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcDao;
import ru.ananev.ohlctest.model.OhlcPeriod;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class OhlcStorageService {

    private final OhlcDao ohlcDao;
    private final ExecutorService persistPool;
//    we can cache last stored Ohlc, in case store is slow
//    private final ConcurrentMap<<instrumentId, OhlcPeriod>, Ohlc> lastUpdates = new ConcurrentHashMap<>();

    public OhlcStorageService(OhlcDao ohlcDao, ExecutorService persistPool) {
        this.ohlcDao = ohlcDao;
        this.persistPool = persistPool;
    }

    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        return ohlcDao.getHistorical(instrumentId, period);
    }

    public Future<?> store(Ohlc ohlc) {
        return persistPool.submit(() -> ohlcDao.store(ohlc));
    }

}
