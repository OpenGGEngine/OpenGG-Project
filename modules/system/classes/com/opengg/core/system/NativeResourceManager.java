package com.opengg.core.system;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NativeResourceManager {
    private static final Object block = new Object();
    private static final Cleaner cleaner = Cleaner.create();

    private static final List<Runnable> cleanEvents = new ArrayList<>();

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
            var tempClean = new ArrayList<Runnable>();
            for(int i = 0; i < cleanEvents.size() * 1.5; i++) {
                tempClean.add(() -> {});
            }
            Collections.copy(tempClean, cleanEvents);
            cleanEvents.clear();
            for(var v : tempClean){
                if(v != null)
                    v.run();
            }
        }
    }
}
