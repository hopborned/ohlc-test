package ru.ananev.ohlctest.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcDao;
import ru.ananev.ohlctest.model.OhlcPeriod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class InmemoryOhlcDao implements OhlcDao {

    private final ConcurrentMap<Key, List<Ohlc>> store = new ConcurrentHashMap<>();

    @Override
    public void store(Ohlc ohlc) {
        store.computeIfAbsent(new Key(ohlc.getInstrumentId(), ohlc.getOhlcPeriod()),
                        key -> new CopyOnWriteArrayList<>())
                .add(ohlc);
    }

    @Override
    public List<Ohlc> getHistorical(long instrumentId, OhlcPeriod period) {
        List<Ohlc> ohlcs = store.get(new Key(instrumentId, period));
        return ohlcs != null ? new ArrayList<>(ohlcs) : new ArrayList<>();
    }

    private static class Key {
        private final long instrumentId;
        private final OhlcPeriod period;

        private Key(long instrumentId, OhlcPeriod period) {
            this.instrumentId = instrumentId;
            this.period = period;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return instrumentId == key.instrumentId && period == key.period;
        }

        @Override
        public int hashCode() {
            return Objects.hash(instrumentId, period);
        }
    }
}
