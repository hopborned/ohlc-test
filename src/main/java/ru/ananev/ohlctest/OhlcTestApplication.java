package ru.ananev.ohlctest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.*;

@SpringBootApplication
public class OhlcTestApplication {

    @Value("${application.persist.max.threads}")
    private int maxPersistThreads;
    @Value("${application.delay.max.seconds}")
    private int maxDelaySeconds;
    @Value("${application.scheduler.max.threads}")
    private int schedulerThreads;


    public static void main(String[] args) {
        SpringApplication.run(OhlcTestApplication.class, args);
    }

    @Bean
    public ExecutorService persistPool() {
        return new ThreadPoolExecutor(1,
                maxPersistThreads,
                maxDelaySeconds,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>());
    }

    @Bean
    public ScheduledExecutorService scheduler() {
        return Executors.newScheduledThreadPool(schedulerThreads);
    }

}
