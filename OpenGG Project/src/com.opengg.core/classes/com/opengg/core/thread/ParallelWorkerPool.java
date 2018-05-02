/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.thread;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.thread.ParallelWorkerPool.WorkerProcessor;
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
    private final Queue<V> finished = new LinkedBlockingQueue<>();
    private final Queue<PriorityEncapsulator<T>> queued = new PriorityBlockingQueue<>(100, (v1, v2) -> {return v2.priority - v1.priority;});
    private final List<WorkerThread<T, V>> workers = new ArrayList<>();
    private final ArrayList<Thread> threads = new ArrayList<>();
    private final WorkerProcessor<T, V> processor;
    private  WorkerDataSource<T> source;
    private final PostOp<V> postop;
    protected int amount;
    
    public ParallelWorkerPool(int amount, WorkerProcessor<T, V> runnable){
        this(amount, runnable, (v) -> {});
    }
    
    public ParallelWorkerPool(int amount, WorkerProcessor<T, V> runnable, PostOp<V> postop){
        this(amount, null, runnable, postop);
        source = () -> {
            synchronized (monitor) {
                while (queued.isEmpty()) {
                    try {
                        monitor.wait();
                    } catch (InterruptedException ex) {
                        if (OpenGG.getEnded()) return null;
                    }
                }

                return queued.poll().t;
            }
            };
    }

    public ParallelWorkerPool(int amount, WorkerDataSource<T> source, WorkerProcessor<T, V> processor, PostOp<V> postop){
        this.amount = amount;
        this.processor = processor;
        this.source = source;
        this.postop = postop;
    }
    
    public void run(){
        for(int i = 0; i < amount; i++){
            WorkerThread<T, V> threadrunnable = new WorkerThread<>(this, source, processor, postop);
            Thread thread = ThreadManager.run(threadrunnable, "WorkerThreadPool:" + i);
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

    void addCompleted(V v){
        this.finished.add(v);
    }

    public interface WorkerProcessor<T, V>{
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
    private final ParallelWorkerPool<T,V> pool;
    private final WorkerProcessor<T, V> processor;
    private final ParallelWorkerPool.WorkerDataSource<T> source;
    private final ParallelWorkerPool.PostOp<V> postop;
    
    WorkerThread(ParallelWorkerPool<T,V> pool,
                 ParallelWorkerPool.WorkerDataSource<T> source,
                 WorkerProcessor<T, V> processor,
                 ParallelWorkerPool.PostOp<V> postop){
        this.pool = pool;
        this.processor = processor;
        this.source = source;
        this.postop = postop;
    }

    @Override
    public void run() {
        while(!OpenGG.getEnded()){
            T value = source.get();
            if(OpenGG.getEnded()) return;
            V returnvalue = processor.process(value);
            pool.addCompleted(returnvalue);
            postop.process(returnvalue);
        }
    }
}
