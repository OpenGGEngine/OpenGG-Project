package com.opengg.core.system;

import java.lang.ref.Cleaner;
import java.util.ArrayList;
import java.util.List;

public class NativeResourceManager {
    private static Cleaner cleaner = Cleaner.create();

    public static void registerNativeResource(NativeResource resource){
        register(resource, resource::destroy);
    }

    public static void register(Object object, Runnable onClean){
        var cleanable = cleaner.register(object, onClean);
    }
}
