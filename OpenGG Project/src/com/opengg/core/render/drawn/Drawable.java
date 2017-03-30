/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;

/**
 *
 * @author Javier
 */
public interface Drawable extends Renderable{
    public void setMatrix(Matrix4f m);
    public Matrix4f getMatrix();
    public boolean hasAdjacency();
    public void destroy();
}
