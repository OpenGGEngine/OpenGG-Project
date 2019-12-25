/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.thread;

import com.opengg.core.GGInfo;

import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *
 * @author Javier
 * @param <T> Input source type
 * @param <V> Output source type
 */
public final class ParallelWorkerPool<T, V> {
    private static final Object monitor = new Object();
    private final Queue<PriorityEncapsulator<T>> queued = new PriorityBlockingQueue<>(100, (v1, v2) -> {return v2.priority - v1.priority;});
    private final List<WorkerThread> workers = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();

    private Supplier<T> source;
    private final Function<T, V> function;
    private final BiConsumer<T, V> postop;

    protected int amount;
    
    public ParallelWorkerPool(int amount, Function<T, V> runnable){
        this(amount, runnable, (t,v) -> {});
    }
    
    public ParallelWorkerPool(int amount, Function<T, V> runnable, BiConsumer<T, V> postop){
        this(amount, null, runnable, postop);
        source = () -> {
            synchronized (monitor) {
                while (queued.isEmpty()) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        if (GGInfo.isEnded()) return null;
                    }
                }

                return queued.poll().t;
            }};
    }

    public ParallelWorkerPool(int amount, Supplier<T> source, Function<T, V> function, BiConsumer<T, V> postop){
        this.amount = amount;
        this.function = function;
        this.source = source;
        this.postop = postop;
    }
    
    public void run(){
        for(int i = 0; i < amount; i++){
            WorkerThread worker = new WorkerThread();
            Thread thread = ThreadManager.runDaemon(worker, "WorkerThreadPool: " + i);
            workers.add(worker);
            threads.add(thread);
        }
    }
    
    public void add(T t){
        queued.add(new PriorityEncapsulator(t, 5));
        synchronized(monitor){
            monitor.notifyAll();
        }
    }
    
    public void add(T t, int priority){
        queued.add(new PriorityEncapsulator(t, priority));
        synchronized(monitor){
            monitor.notifyAll();
        }
    }

    public List<T> getQueue(){
        return Collections.unmodifiableList(queued.stream().map(s -> s.t).collect(Collectors.toUnmodifiableList()));
    }

    public Function<T, V> getFunction() {
        return function;
    }

    public Supplier<T> getSource() {
        return source;
    }

    public BiConsumer<T, V> getPostop() {
        return postop;
    }

    private class PriorityEncapsulator<T>{
        T t;
        int priority;
        
        PriorityEncapsulator(T t, int priority){
            this.t = t;
            this.priority = priority;
        }
    }

    private class WorkerThread implements Runnable {
        @Override
        public void run() {
            while(true){
                T t = getSource().get();
                V v = getFunction().apply(t);
                getPostop().accept(t, v);
            }
        }
    }
}

