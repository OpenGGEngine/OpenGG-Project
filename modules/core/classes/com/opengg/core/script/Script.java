package com.opengg.core.script;

import com.opengg.core.world.components.ScriptComponent;

import java.util.function.BiConsumer;

public abstract class Script implements BiConsumer<ScriptComponent, Float> {
    private final String version;
    private final String application;

    public Script(String version, String application){
        this.version = version;
        this.application = application;
    }

    /**
     * Returns the game version used to create this script at compilation time
     * @return
     */
    public final String getVersion(){
        return version;
    }

    /**
     * Returns the application name used to create this script at compilation time
     * @return
     */
    public final String getApplication() {
        return application;
    }
}
