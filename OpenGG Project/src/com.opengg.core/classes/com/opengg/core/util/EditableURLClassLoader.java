package com.opengg.core.util;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;

public class EditableURLClassLoader extends URLClassLoader {
    public EditableURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public EditableURLClassLoader(URL[] urls) {
        super(urls);
    }

    public EditableURLClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public EditableURLClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    public EditableURLClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(name, urls, parent, factory);
    }

    @Override
    public void addURL(URL url){
        super.addURL(url);
    }
}
