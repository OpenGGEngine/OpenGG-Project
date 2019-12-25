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
public class IncompatibleWindowFormatException  extends RuntimeException{
    private static final long serialVersionUID = 1L;
	
	public IncompatibleWindowFormatException() {
		super();
	}
	
	public IncompatibleWindowFormatException(String message) {
		super(message);
	}

	public IncompatibleWindowFormatException(Throwable ex) {
		super(ex);
	}

	public IncompatibleWindowFormatException(String message, Throwable ex) {
		super(message, ex);
	}
}
