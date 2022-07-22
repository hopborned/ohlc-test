package ru.ananev.ohlctest.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ananev.ohlctest.model.OhlcPeriod;

@Service
public class PeriodService {

    private final int delaySeconds;

    public PeriodService(@Value("${application.delay.max.seconds}") int delaySeconds) {
        this.delaySeconds = delaySeconds;
    }

    public long millisToExpire(long timestamp, OhlcPeriod period) {
        long millis = period.getMillis();
        return (timestamp / millis) * millis + millis - timestamp + delaySeconds * 1000L;
    }

    public long periodStart(long timestamp, OhlcPeriod period) {
        long millis = period.getMillis();
        return (timestamp / millis) * millis;
    }


}
