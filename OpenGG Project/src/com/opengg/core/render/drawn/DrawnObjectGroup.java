/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.Matrix4f;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class DrawnObjectGroup implements Drawable {

    ArrayList<Drawable> objs = new ArrayList<>();

    //The list of materials are in order so the nth value in list objs
    //corresponds to the nth value in list materials
    

    public DrawnObjectGroup(ArrayList<Drawable> objs) {
        this.objs = objs;
    }
    

    @Override
    public void saveShadowMVP() {
        objs.stream().forEach((d) -> {
            d.saveShadowMVP();
        });
    }

    public void setShaderMatrix(Matrix4f m) {
//        objs.stream().forEach((d) -> {
//            d.setShaderMatrix(m);
//        });
    }

    @Override
    public void draw() {
        objs.stream().forEach((d) -> {
            d.draw();
        });
    }

    @Override
    public void drawPoints() {
        objs.stream().forEach((d) -> {
            d.drawPoints();
        });
    }

    @Override
    public void setMatrix(Matrix4f model) {
        objs.stream().forEach((d) -> {
            d.setMatrix(model);
        });
    }

    @Override
    public Matrix4f getMatrix() {
        return new Matrix4f();
    }

    @Override
    public void destroy() {
        objs.stream().forEach((d) -> {
            d.destroy();
        });
    }

}
