package com.opengg.core.script;

import java.net.URL;
import java.net.URLClassLoader;

public class ScriptClassLoader extends URLClassLoader {
    public ScriptClassLoader(URL urls) {
        super(new URL[]{urls});
    }
}
