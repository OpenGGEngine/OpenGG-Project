/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;

/**
 *
 * @author ethachu19
 */
public interface Renderable extends Positioned{
    /**
     * Render the Current Component
     */
    public void render();
    public void setScale(Vector3f v);
    public Vector3f getScale();
    public Drawable getDrawable();
}
