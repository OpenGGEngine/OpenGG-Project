/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;

/**
 *
 * @author Javier
 */
public interface Drawable {
    public void draw();
    public void drawPoints();
    public void setMatrix(Matrix4f m);
    public Matrix4f getMatrix();
    public void destroy();
}
