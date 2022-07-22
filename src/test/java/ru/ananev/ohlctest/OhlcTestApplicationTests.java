package ru.ananev.ohlctest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.ananev.ohlctest.model.OhlcService;
import ru.ananev.ohlctest.model.Quote;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

@SpringBootTest
public
class OhlcTestApplicationTests {

    @Test
    void contextLoads() {
    }

    public static class QImpl implements Quote {
        private final long instId;
        private final double price;
        private final long time;


        public QImpl(long instId, double price, long time) {
            this.instId = instId;
            this.price = price;
            this.time = time;
        }

        @Override
        public double getPrice() {
            return price;
        }

        @Override
        public long getInstrumentId() {
            return instId;
        }

        @Override
        public long getUtcTimestamp() {
            return time;
        }
    }

}
