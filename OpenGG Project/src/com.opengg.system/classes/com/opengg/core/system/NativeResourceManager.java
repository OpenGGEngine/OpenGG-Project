package com.opengg.core.system;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.IntStream;

public class NativeResourceManager {
    private static final Object block = new Object();
    private static Cleaner cleaner = Cleaner.create();

    private static List<Runnable> cleanEvents = new ArrayList<>();

    public static void registerNativeResource(NativeResource resource){
        var runnable = resource.onDestroy();
        register(resource, runnable);
    }

    public static void register(Object object, Runnable onClean){
        if(onClean == null) return;
        synchronized (block){
            cleaner.register(object, () -> cleanEvents.add(onClean));
        }
    }

    public static void runQueuedFinalization() {
        synchronized (block) {
            var tempClean = List.copyOf(cleanEvents);
            cleanEvents.clear();
            tempClean.forEach(Runnable::run);
        }
    }

}
