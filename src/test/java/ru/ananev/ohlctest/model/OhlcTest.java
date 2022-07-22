package ru.ananev.ohlctest.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OhlcTest {

    @Test
    @DisplayName("highPrice updated when ohlc updated with higher price")
    void highPriceUpdated() {
        Ohlc ohlc = new Ohlc(1, 1., 1000, OhlcPeriod.D1);
        Ohlc update = ohlc.update(2.);
        assertThat(update).isEqualTo(
                new Ohlc(1, 1, 2., 1., 2., OhlcPeriod.D1, 1000)
        );
    }

    @Test
    @DisplayName("lowPrice updated when ohlc updated with lower price")
    void lowPriceUpdated() {
        Ohlc ohlc = new Ohlc(1, 4., 1000, OhlcPeriod.D1);
        Ohlc update = ohlc.update(3.);
        assertThat(update).isEqualTo(
                new Ohlc(1, 4., 4., 3., 3., OhlcPeriod.D1, 1000)
        );
    }

    @Test
    @DisplayName("Ohlc created with all all prices equal to price")
    void ohlcCreated() {
        Ohlc ohlc = new Ohlc(1, 5., 1000, OhlcPeriod.D1);
        assertThat(ohlc).isEqualTo(
                new Ohlc(1, 5., 5., 5., 5., OhlcPeriod.D1, 1000)
        );
    }
}