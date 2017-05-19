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
public class InvalidShaderException
    extends RuntimeException{
    private static final long serialVersionUID = 1L;
	
    public InvalidShaderException() {
            super();
    }

    public InvalidShaderException (String message) {
            super(message);
    }

    public InvalidShaderException (Throwable ex) {
            super(ex);
    }

    public InvalidShaderException (String message, Throwable ex) {
            super(message, ex);
    }
}
