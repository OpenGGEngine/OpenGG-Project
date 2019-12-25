/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

/**
 * Represents an object that has the ability to be rendered
 * @author Javier
 */
public interface Renderable {
    /**
     * Renders this to the current framebuffer
     */
    void render();
}
