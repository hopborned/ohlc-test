package ru.ananev.ohlctest.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.ananev.ohlctest.model.OhlcPeriod;

class PeriodServiceTest {

    @Test
    void millisToExpireWithZeroDelay() {
        PeriodService periodService = new PeriodService(0);
        for (OhlcPeriod period : OhlcPeriod.values()) {
            long millis = periodService.millisToExpire(3, period);
            Assertions.assertThat(millis).isEqualTo(period.getMillis() - 3);
        }
    }

    @Test
    void millisToExpireWithNonDelay() {
        PeriodService periodService = new PeriodService(1);
        for (OhlcPeriod period : OhlcPeriod.values()) {
            long millis = periodService.millisToExpire(3, period);
            Assertions.assertThat(millis).isEqualTo(period.getMillis() - 3 + 1000);
        }
    }

    @Test
    void periodStart() {
        PeriodService periodService = new PeriodService(0);
        for (OhlcPeriod period : OhlcPeriod.values()) {
            long millis = periodService.periodStart(period.getMillis() + 1, period);
            Assertions.assertThat(millis).isEqualTo(period.getMillis());
        }

    }
}