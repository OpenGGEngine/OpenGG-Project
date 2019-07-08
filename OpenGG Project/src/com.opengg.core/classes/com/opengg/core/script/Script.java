package com.opengg.core.script;

import com.opengg.core.world.components.Component;

import java.util.function.BiConsumer;

public abstract class Script implements BiConsumer<Component, Float> {
    private final String version;
    private final String application;

    public Script(String version, String application){
        this.version = version;
        this.application = application;
    }

    public final String getVersion(){
        return version;
    }

    public final String getApplication() {
        return application;
    }
}
