package ru.ananev.ohlctest.impl.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ananev.ohlctest.OhlcTestApplicationTests;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SimpleOhlcServiceTest {
    @Mock
    ScheduledExecutorService executorService;
    @Mock
    OhlcStorageService ohlcStorageService;
    @Mock
    PeriodService periodService;
    @InjectMocks
    SimpleOhlcService ohlcService;

    @Test
    void getCurrent() {
        when(ohlcService.getHistorical(1, OhlcPeriod.M1)).thenReturn(new ArrayList<>());
        when(periodService.periodStart(0L, OhlcPeriod.M1)).thenReturn(0L);
        ohlcService.onQuote(new OhlcTestApplicationTests.QImpl(1, 1., 0));

        List<Ohlc> historicalAndCurrent = ohlcService.getHistoricalAndCurrent(1, OhlcPeriod.M1);

        Assertions.assertThat(historicalAndCurrent).isEqualTo(Collections.singletonList(
                new Ohlc(1, 1., 0, OhlcPeriod.M1)
        ));
        verify(ohlcStorageService).getHistorical(1, OhlcPeriod.M1);
    }

    @Test
    void getHistorical() {
        ohlcService.getHistorical(1, OhlcPeriod.M1);

        Mockito.verify(ohlcStorageService).getHistorical(1, OhlcPeriod.M1);
    }

    @Test
    void getHistoricalAndCurrent() {
        ohlcService.getHistoricalAndCurrent(1, OhlcPeriod.M1);

        Mockito.verify(ohlcStorageService).getHistorical(1, OhlcPeriod.M1);
    }

    @Test
    void onQuote() {
    }
}