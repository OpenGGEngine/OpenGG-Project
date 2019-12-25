package com.opengg.core.exceptions;

public class DataBindingException extends RuntimeException{
    public DataBindingException() {
        super();
    }

    public DataBindingException(String message) {
        super(message);
    }

    public DataBindingException(Throwable ex) {
        super(ex);
    }

    public DataBindingException(String message, Throwable ex) {
        super(message, ex);
    }
}
