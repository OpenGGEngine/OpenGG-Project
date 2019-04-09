package com.opengg.core.system;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;

public class NativeResourceManager {
    private static Cleaner cleaner = Cleaner.create();

    private static List<Runnable> cleanEvents = new ArrayList<>();

    public static void registerNativeResource(NativeResource resource){
        var runnable = resource.onDestroy();
        register(resource, runnable);
    }

    public static void register(Object object, Runnable onClean){
        var cleanable = cleaner.register(object, () -> cleanEvents.add(onClean));
    }

    public static void runQueuedFinalization() {
        var tempClean = List.copyOf(cleanEvents);
        cleanEvents.clear();
        tempClean.forEach(Runnable::run);
    }

}
