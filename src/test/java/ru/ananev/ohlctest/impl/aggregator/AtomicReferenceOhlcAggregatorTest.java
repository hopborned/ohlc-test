package ru.ananev.ohlctest.impl.aggregator;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ananev.ohlctest.OhlcTestApplicationTests;
import ru.ananev.ohlctest.impl.service.OhlcStorageService;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcPeriod;
import ru.ananev.ohlctest.util.CompletedScheduledFuture;
import ru.ananev.ohlctest.util.PeriodService;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AtomicReferenceOhlcAggregatorTest {

    @Mock
    ScheduledExecutorService executorService;
    @Mock
    OhlcStorageService ohlcStorageService;
    @Mock
    PeriodService periodService;

    AtomicReferenceOhlcAggregator aggregator;

    @Test
    @DisplayName("returns null if no quotes received")
    void getByPeriodReturnsNullWhenEmpty() {
        aggregator = new AtomicReferenceOhlcAggregator(OhlcPeriod.M1,
                executorService,
                ohlcStorageService,
                periodService);

        Ohlc current = aggregator.getCurrent();

        assertThat(current).isNull();
    }

    @Test
    @DisplayName("returns Ohlc when quote added")
    void addQuote() {
        aggregator = new AtomicReferenceOhlcAggregator(OhlcPeriod.M1,
                executorService,
                ohlcStorageService,
                new PeriodService(1000));

        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 1., 1));
        Ohlc current = aggregator.getCurrent();

        assertThat(current).isEqualTo(
                new Ohlc(1, 1., 0, OhlcPeriod.M1)
        );
    }

    @Test
    @DisplayName("returns new Ohlc when quote from next period added")
    void addQuoteFromNextPeriod() {
        aggregator = new AtomicReferenceOhlcAggregator(OhlcPeriod.M1,
                executorService,
                ohlcStorageService,
                new PeriodService(1000));
        when(executorService.schedule((Runnable) any(), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .thenReturn(new CompletedScheduledFuture<>(null));

        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 1., 1000));
        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 2., 61000));
        Ohlc current = aggregator.getCurrent();

        assertThat(current).isEqualTo(
                new Ohlc(1, 2., 60000, OhlcPeriod.M1)
        );
    }

    @Test
    @DisplayName("ohlc updated on timeout")
    void ohlcUpdatedOnTimeout() throws InterruptedException, ExecutionException {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService spy = spy(scheduler);
        aggregator = new AtomicReferenceOhlcAggregator(OhlcPeriod.M1, spy,
                ohlcStorageService, periodService);

        when(periodService.periodStart(59000, OhlcPeriod.M1)).thenReturn(0L);
        when(periodService.millisToExpire(59000, OhlcPeriod.M1)).thenReturn(1L);
        AtomicReference<Object> a = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            Object o = invocationOnMock.callRealMethod();
            a.set(o);
            return o;
        }).when(spy).schedule((Runnable) any(), eq(1L), eq(TimeUnit.MILLISECONDS));

        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 1., 59 * 1000));
        //wait update to be done
        ((ScheduledFuture<?>) a.get()).get();
        scheduler.shutdown();
        verify(spy).schedule((Runnable) any(), eq(1L), eq(TimeUnit.MILLISECONDS));
        verify(ohlcStorageService).store(new Ohlc(1, 1., 0, OhlcPeriod.M1));
        assertThat(aggregator.getCurrent()).isNull();
    }

    @Test
    @DisplayName("ohlc not updated on schedule if updated earlier by quote")
    void ohlcNotUpdateOnSchedule() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        ScheduledExecutorService spy = spy(scheduler);
        aggregator = new AtomicReferenceOhlcAggregator(OhlcPeriod.M1, spy,
                ohlcStorageService, periodService);

        when(periodService.periodStart(59000, OhlcPeriod.M1)).thenReturn(0L);
        when(periodService.periodStart(61000, OhlcPeriod.M1)).thenReturn(60000L);
        when(periodService.millisToExpire(59000, OhlcPeriod.M1)).thenReturn(1_000_000L);
        when(periodService.millisToExpire(61000, OhlcPeriod.M1)).thenReturn(2_000_000L);
        AtomicReference<Object> a = new AtomicReference<>();
        doAnswer(invocationOnMock -> {
            Object o = invocationOnMock.callRealMethod();
            a.set(o);
            return o;
        }).when(spy).schedule((Runnable) any(), eq(1_000_000L), eq(TimeUnit.MILLISECONDS));

        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 1., 59 * 1000));
        aggregator.update(new OhlcTestApplicationTests.QImpl(1, 1., 61 * 1000));
        //wait update to be done

        ScheduledFuture<?> scheduledFuture = (ScheduledFuture<?>) a.get();
        assertThat(scheduledFuture.isCancelled()).isTrue();
        assertThat(aggregator.getCurrent()).isEqualTo(
                new Ohlc(1, 1., 60000, OhlcPeriod.M1)
        );
        verify(ohlcStorageService).store(new Ohlc(1, 1., 0, OhlcPeriod.M1));
        scheduler.shutdownNow();
    }


}