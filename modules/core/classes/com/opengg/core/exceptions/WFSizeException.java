
package com.opengg.core.exceptions;

/**
 * This exception is used to indicate that a WaveFront
 * resource is too large to load.
 *
 * 
 */
public class WFSizeException extends WFException {

    private static final long serialVersionUID = 1L;

    public WFSizeException() {
        super();
    }

    public WFSizeException(String message) {
        super(message);
    }

    public WFSizeException(Throwable ex) {
        super(ex);
    }

    public WFSizeException(String message, Throwable ex) {
        super(message, ex);
    }
}
