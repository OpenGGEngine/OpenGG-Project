/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.thread;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.thread.ParallelWorkerPool.WorkerRunnable;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 *
 * @author Javier
 * @param <T> Input source type
 * @param <V> Output source type
 */
public final class ParallelWorkerPool<T, V> {
    private static final Object monitor = new Object();
    protected Queue<V> finished = new LinkedBlockingQueue<>();
    protected Queue<PriorityEncapsulator<T>> queued = new PriorityBlockingQueue<>(100, (v1, v2) -> {return v2.priority - v1.priority;});
    protected List<WorkerThread<T, V>> workers = new ArrayList<>();
    protected ArrayList<Thread> threads = new ArrayList<>();
    protected WorkerRunnable<T, V> runnable;
    protected WorkerDataSource<T> source;
    protected PostOp<V> postop;
    protected int amount;
    
    public ParallelWorkerPool(int amount, WorkerRunnable<T, V> runnable){
        this(amount, runnable, null);
        postop = (v) -> {};
    }
    
    public ParallelWorkerPool(int amount, WorkerRunnable<T, V> runnable, PostOp<V> postop){
        this(amount, null, runnable, postop);
        source = () -> {
                while (!OpenGG.getEnded()) {
                    synchronized (monitor) {
                        while (queued.isEmpty()) {
                            try {
                                monitor.wait();
                            } catch (InterruptedException ex) {
                                if(OpenGG.getEnded()) return null;
                            }
                        }

                        return queued.poll().t;
                    }
                }
                return null;
            };
    }

    public ParallelWorkerPool(int amount, WorkerDataSource<T> source, WorkerRunnable<T, V> runnable, PostOp<V> postop){       
        this.amount = amount;
        this.runnable = runnable;
        this.source = source;
        this.postop = postop;
    }
    
    public void run(){
        for(int i = 0; i < amount; i++){
            WorkerThread<T, V> threadrunnable = new WorkerThread<>(this);
            Thread thread = ThreadManager.runRunnable(threadrunnable, "WorkerThreadPool:" + i);
            workers.add(threadrunnable);
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

    public interface WorkerRunnable<T, V>{
        public V process(T t);
    }
    
    public interface PostOp<V>{
        public void process(V v);
    }
    
    public interface WorkerDataSource<T>{
        public T get();
    }
    
    class PriorityEncapsulator<T>{
        T t;
        int priority;
        
        PriorityEncapsulator(T t, int priority){
            this.t = t;
            this.priority = priority;
        }
    }
}

class WorkerThread<T, V> implements Runnable {
    ParallelWorkerPool<T, V> pool;
    
    public WorkerThread(ParallelWorkerPool<T, V> pool){
        this.pool = pool;
    }

    @Override
    public void run() {
        while(!OpenGG.getEnded()){
            T value = pool.source.get();
            if(OpenGG.getEnded()) return;
            V returnvalue = pool.runnable.process(value);
            pool.finished.add(returnvalue);
            pool.postop.process(returnvalue);
        }
    }
}
