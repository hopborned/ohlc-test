package ru.ananev.ohlctest.impl.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ananev.ohlctest.model.Ohlc;
import ru.ananev.ohlctest.model.OhlcDao;
import ru.ananev.ohlctest.model.OhlcPeriod;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OhlcStorageServiceTest {

    @Mock
    OhlcDao dao;

    ExecutorService executorService = Executors.newSingleThreadExecutor();

    OhlcStorageService ohlcStorageService;

    @BeforeEach
    void setUp() {
        ohlcStorageService = new OhlcStorageService(dao, executorService);
    }

    @Test
    void getHistorical() {
        ohlcStorageService.getHistorical(1, OhlcPeriod.M1);

        verify(dao).getHistorical(1, OhlcPeriod.M1);
    }

    @Test
    void store() throws ExecutionException, InterruptedException {
        Ohlc ohlc = new Ohlc(1, 1., 0, OhlcPeriod.M1);

        Future<?> store = ohlcStorageService.store(ohlc);

        store.get();
        verify(dao).store(ohlc);
        executorService.shutdown();
    }
}