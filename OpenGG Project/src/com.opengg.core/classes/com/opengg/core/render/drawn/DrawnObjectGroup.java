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
        this.objs = List.copyOf(objs);
    }
    
    public void add(Drawable d){
        objs.add(d);
    }

    public void remove(Drawable d){
        objs.remove(d);
    }

    @Override
    public void render() {
        for(Drawable d : objs){
            d.render();
        }
    }

    @Override
    public void setMatrix(Matrix4f model) {
        for(Drawable d : objs){
            d.setMatrix(model);
        }
    }

    @Override
    public void destroy() {
        for(Drawable d : objs){
            d.destroy();
        }
    }
}
