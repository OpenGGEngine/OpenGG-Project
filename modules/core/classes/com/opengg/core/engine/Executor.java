package com.opengg.core.engine;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Executor {
    private final List<ExecutorContainer> containers= new ArrayList<>();
    private static final List<ExecutorContainer> newContainers = new ArrayList<>();

    private final Thread current;
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
        current = Thread.currentThread();
    }

    public void update(float time) {
        containers.addAll(newContainers);
        newContainers.clear();

        var doneList = containers.stream()
                .filter(Objects::nonNull)
                .peek(c -> c.time -= time)
                .filter(ExecutorContainer::isComplete)
                .collect(Collectors.toList());

        containers.removeAll(doneList);

        doneList.forEach(ExecutorContainer::execute);
        //if(!tempcontainers.isEmpty()) System.out.println(Arrays.toString(tempcontainers.get(0).source));
        if(!newContainers.isEmpty()) update(0);
    }

    /**
     * Runs the given function at the given Instant on the main thread
     * @param instant Instant at which the function will be run, with a maximum of 1 frame of delay
     * @param exec Runnable to run at the given Instant
     * @return {@link Sleeper} to wait on the runnable
     */
    public static Sleeper at(Instant instant, Runnable exec) {
        return in(Instant.now().until(instant, ChronoUnit.MILLIS)/1000f, exec);
    }

    /**
     * Runs the given function after every period on the main thread
     * @param period Amount of time between runs of the Runnable
     * @param exec Runnable to run during each period
     */
    public static void every(TemporalAmount period, Runnable exec) {
        new Runnable() {
            @Override
            public void run() {
                in(period, this);
                exec.run();
            }
        }.run();
    }

    /**
     * Runs the given Runnable after {@code secs} seconds on the main thread
     * @param secs Time to wait before isRunning the function
     * @param exec Runnable to run after the tgiven time
     * @return {@link Sleeper} to wait on the runnable
     */
    public static Sleeper in(float secs, Runnable exec) {
        return getExecutor().inInternal(secs, exec);
    }

    /**
     * Runs the given Runnable after {@code period} time on the main thread
     * @param period Time to wait before isRunning the function
     * @param exec Runnable to run after the tgiven time
     * @return {@link Sleeper} to wait on the runnable
     */
    public static Sleeper in(TemporalAmount period, Runnable exec) {
        return at(ZonedDateTime.now().plus(period).toInstant(), exec);
    }

    /**
     * Runs the given Runnable on the main thread
     * @param exec Runnable to run
     * @return {@link Sleeper} to wait on the runnable
     */
    public static Sleeper async(Runnable exec){
        return getExecutor().queue(-1f, exec);
    }

    /**
     * Runs the given Runnable on the main thread, and sleeps the current thread until it is done
     * <br>
     * If this method is run while alrady on the main thread, this will simply run the runnable with no delay
     * @param exec Runnable to run
     */
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

        return queue(secs, exec);
    }

    private Sleeper queue(float secs, Runnable exec){
        var container = new ExecutorContainer(exec, secs, Thread.currentThread().getStackTrace());
        newContainers.add(container);
        return container.sleeper;
    }

    private static class ExecutorContainer {
        private final Runnable runnable;
        private final Sleeper sleeper;
        private float time;
        private final StackTraceElement[] source;

        private ExecutorContainer(Runnable runnable, float time, StackTraceElement[] source) {
            this.runnable = runnable;
            this.sleeper = new Sleeper();
            this.time = time;
            this.source = source;
        }

        private boolean isComplete() {
            return time < 0;
        }

        private void execute() {
            try{
                runnable.run();
            }catch (Exception e){
                var newException = new RuntimeException("Caused in executable:");
                newException.setStackTrace(this.source);
                e.addSuppressed(newException);
                throw e;
            }
            sleeper.awaken();
        }
    }

    /**
     * Class used to wait for events passed to the Executor system
     */
    public static class Sleeper {
        private final Object lock = new Object();

        private Sleeper() {
        }

        private void awaken() {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        /**
         * Sleeps the current thread until the command has been executed
         */
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

    public static class UselessSleeper extends Sleeper {
        @Override
        public void waitUntilComplete() {
        }
    }
}
