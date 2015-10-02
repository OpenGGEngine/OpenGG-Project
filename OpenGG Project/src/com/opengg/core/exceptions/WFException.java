

package com.opengg.core.exceptions;

import java.io.IOException;

/**
 * This is a generic exception that indicates a problem with a WaveFront 
 * resource.
 * <p>
 * Instances of this or sub-classes will generally be thrown during
 * loading or saving of WaveFront resources.
 *
 */
public class WFException extends IOException {

    private static final long serialVersionUID = 1L;

    public WFException() {
        super();
    }

    public WFException(String message) {
        super(message);
    }

    public WFException(Throwable ex) {
        super(ex);
    }

    public WFException(String message, Throwable ex) {
        super(message, ex);
    }
    
}
