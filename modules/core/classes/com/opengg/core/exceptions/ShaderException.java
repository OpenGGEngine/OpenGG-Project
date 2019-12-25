/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.exceptions;

/**
 * Exception thrown during an issue compiling/using a GLSL/VUlkan shader
 * @author Javier
 */
public class ShaderException
    extends RenderException{
    private static final long serialVersionUID = 1L;
	
    public ShaderException() {
            super();
    }

    public ShaderException (String message) {
            super(message);
    }

    public ShaderException (Throwable ex) {
            super(ex);
    }

    public ShaderException (String message, Throwable ex) {
            super(message, ex);
    }
}
