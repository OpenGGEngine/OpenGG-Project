package com.opengg.core.exceptions;

public class ClassInstantiationException extends Exception{
    private static final long serialVersionUID = 1L;

    public ClassInstantiationException() {
        super();
    }

    public ClassInstantiationException(String message) {
        super(message);
    }

    public ClassInstantiationException(Throwable ex) {
        super(ex);
    }

    public ClassInstantiationException(String message, Throwable ex) {
        super(message, ex);
    }
}
