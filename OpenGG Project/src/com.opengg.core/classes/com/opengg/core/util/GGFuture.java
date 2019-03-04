package com.opengg.core.util;

import com.opengg.core.engine.OpenGG;

import java.util.function.Consumer;

public class GGFuture<T> {
    private Object monitor = new Object();
    private boolean done;
    private T val;
    private Consumer<T> func = v -> {};

    public boolean exists(){
        return done;
    }

    public GGFuture set(T resource){
        this.val = resource;
        this.done = true;

        synchronized(monitor){
            monitor.notifyAll();
        }

        OpenGG.asyncExec(() -> func.accept(resource));

        return this;
    }


    public T get(){
        if(!done){
            synchronized(monitor){
                try{
                    monitor.wait();
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        }

        return val;
    }

    public void whenComplete(Consumer<T> res){
        this.func = res;
    }
}
