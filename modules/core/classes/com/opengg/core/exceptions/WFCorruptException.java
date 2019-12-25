

package com.opengg.core.exceptions;

/**
 * This exception is used to indicate a corrupt or invalid
 * WaveFront resource.
 *
 * 
 */
public class WFCorruptException extends WFException {

	private static final long serialVersionUID = 1L;
	
	public WFCorruptException() {
		super();
	}
	
	public WFCorruptException(String message) {
		super(message);
	}

	public WFCorruptException(Throwable ex) {
		super(ex);
	}

	public WFCorruptException(String message, Throwable ex) {
		super(message, ex);
	}

}
