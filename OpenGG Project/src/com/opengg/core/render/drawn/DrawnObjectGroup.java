/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class DrawnObjectGroup implements Drawable {

    List<Drawable> objs = new ArrayList<>();


    
    public DrawnObjectGroup(){
        
    }
    
    public DrawnObjectGroup(List<Drawable> objs) {
        this.objs = objs;
    }
    
    public void add(Drawable d){
        objs.add(d);
    }

    public void remove(Drawable d){
        objs.remove(d);
    }
    
    @Override
    public void saveShadowMVP() {
        objs.stream().forEach((d) -> {
            d.saveShadowMVP();
        });
    }

    @Override
    public void draw() {
        objs.stream().forEach((d) -> {
            d.draw();
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
