package ru.ananev.ohlctest.impl.aggregator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.ananev.ohlctest.OhlcTestApplicationTests;
import ru.ananev.ohlctest.impl.service.OhlcStorageService;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.concurrent.Executors;

class AtomicReferenceAggregatorHolderTest {


    @Test
    @DisplayName("returns null for all periods if no quotes added")
    void getByPeriodReturnsNullWhenEmpty() {
        AtomicReferenceAggregatorHolder holder = new AtomicReferenceAggregatorHolder(Mockito.mock(OhlcStorageService.class),
                Executors.newScheduledThreadPool(0), new PeriodService(10));

        for (OhlcPeriod period : OhlcPeriod.values()) {
            Assertions.assertThat(holder.getByPeriod(period)).isNull();
        }
    }

    @Test
    @DisplayName("returns Ohlc for all periods when quote added")
    void addQuote() {
        AtomicReferenceAggregatorHolder holder = new AtomicReferenceAggregatorHolder(
                Mockito.mock(OhlcStorageService.class),
                Executors.newScheduledThreadPool(0), new PeriodService(10));

        holder.addQuote(new OhlcTestApplicationTests.QImpl(1, 1., 1));

        for (OhlcPeriod period : OhlcPeriod.values()) {
            Assertions.assertThat(holder.getByPeriod(period)).isEqualTo(
                    new Ohlc(1, 1., 0, period)
            );
        }
    }

}