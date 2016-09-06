/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

/**
 *
 * @author ethachu19
 */
public interface Renderable extends Positioned{
    /**
     * Render the Current Component
     */
    public void render();
}
