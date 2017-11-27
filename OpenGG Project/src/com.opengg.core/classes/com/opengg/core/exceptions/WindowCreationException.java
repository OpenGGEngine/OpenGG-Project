/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.exceptions;

/**
 *
 * @author Javier
 */
public class WindowCreationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
	
	public WindowCreationException() {
		super();
	}
	
	public WindowCreationException(String message) {
		super(message);
	}

	public WindowCreationException(Throwable ex) {
		super(ex);
	}

	public WindowCreationException(String message, Throwable ex) {
		super(message, ex);
	}
}
