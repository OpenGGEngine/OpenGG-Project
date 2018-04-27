/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.exceptions;

/**
 * @author Javier
 */
public class RenderException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public RenderException() {
            super();
        }

    public RenderException (String message) {
            super(message);
        }

    public RenderException (Throwable ex) {
            super(ex);
        }

    public RenderException (String message, Throwable ex) {
            super(message, ex);
        }
}
