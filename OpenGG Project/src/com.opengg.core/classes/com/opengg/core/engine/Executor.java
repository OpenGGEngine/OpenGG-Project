package com.opengg.core.engine;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Executor {
    private List<ExecutorContainer> containers;
    private Thread current;
    private static Executor executor;

    public static void initialize() {
        executor = new Executor();
    }

    public static Executor getExecutor() {
        if (executor == null)
            executor = new Executor();
        return executor;
    }

    private Executor() {
        containers = new ArrayList<>();
        current = Thread.currentThread();
    }

    public void update(float time) {
        var tlist = containers.stream()
                .peek(c -> c.time -= time)
                .filter(ExecutorContainer::isComplete)
                .collect(Collectors.toList());

        containers.removeAll(tlist);

        tlist.forEach(ExecutorContainer::execute);
    }

    public static Sleeper at(Instant instant, Runnable exec) {
        return in(Instant.now().until(instant, ChronoUnit.MILLIS)/1000f, exec);
    }

    public static void every(TemporalAmount period, Runnable exec) {
        new Runnable() {
            @Override
            public void run() {
                in(period, this);
                exec.run();
            }
        }.run();
    }

    public static Sleeper in(float secs, Runnable exec) {
        return getExecutor().inInternal(secs, exec);
    }

    public static Sleeper in(TemporalAmount period, Runnable exec) {
        return at(ZonedDateTime.now().plus(period).toInstant(), exec);
    }

    public static Sleeper async(Runnable exec){
        return in(0.0001f, exec);
    }

    public static void sync(Runnable exec){
        if(!executor.current.equals(Thread.currentThread()))
            in(0.001f, exec).waitUntilComplete();
        else
            exec.run();
    }

    private Sleeper inInternal(float secs, Runnable exec) {
        if (secs <= 0) {
            exec.run();
            return new UselessSleeper();
        }

        var container = new ExecutorContainer(exec, secs);
        containers.add(container);
        return container.sleeper;
    }

    private class ExecutorContainer {
        private Runnable runnable;
        private Sleeper sleeper;
        private float time;

        private ExecutorContainer(Runnable runnable, float time) {
            this.runnable = runnable;
            this.sleeper = new Sleeper();
            this.time = time;
        }

        private boolean isComplete() {
            return time < 0;
        }

        private void execute() {
            runnable.run();
            sleeper.awaken();
        }
    }

    public class Sleeper {
        private final Object lock = new Object();

        private Sleeper() {
        }

        private void awaken() {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        public void waitUntilComplete() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class UselessSleeper extends Sleeper {
        @Override
        public void waitUntilComplete() {
        }
    }
}
