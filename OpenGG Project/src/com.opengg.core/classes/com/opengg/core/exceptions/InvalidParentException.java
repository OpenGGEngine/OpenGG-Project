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
public class InvalidParentException 
    extends RuntimeException{
    private static final long serialVersionUID = 1L;
	
    public InvalidParentException() {
            super();
    }

    public InvalidParentException (String message) {
            super(message);
    }

    public InvalidParentException (Throwable ex) {
            super(ex);
    }

    public InvalidParentException (String message, Throwable ex) {
            super(message, ex);
    }
}

