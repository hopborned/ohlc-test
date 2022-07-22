package ru.ananev.ohlctest.util;

import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class CompletedScheduledFuture<T> implements ScheduledFuture<T> {
    private final T value;

    public CompletedScheduledFuture(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    public T get(long timeout, TimeUnit unit) {
        return value;
    }

    public boolean cancel() {
        return false;
    }

    public boolean isDone() {
        return true;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    public boolean isCancelled() {
        return false;
    }

    public long getDelay(TimeUnit unit) {
        return 0;
    }


    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
}