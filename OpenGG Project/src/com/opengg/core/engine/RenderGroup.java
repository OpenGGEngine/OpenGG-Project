/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.world.components.Renderable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderGroup {
    List<DrawableContainer> items = new ArrayList<>();
    
    boolean transparency = false;
    boolean distancefield;

    public boolean isText() {
        return distancefield;
    }

    public void setText(boolean distancefield) {
        this.distancefield = distancefield;
    }
    boolean shadows = true;
    int order = 5;
    Mode mode = Mode.OBJECT;
    
    public boolean isTransparent() {
        return transparency;
    }

    public void setTransparent(boolean transparency) {
        this.transparency = transparency;
    }

    public boolean ifCastsShadows() {
        return shadows;
    }

    public void setCastShadows(boolean shadows) {
        this.shadows = shadows;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }
        
    public RenderGroup add(DrawableContainer container){
        items.add(container);
        return this;
    }
    
    public RenderGroup add(Drawable d){
        items.add(new DrawableContainer(d));
        return this;
    }
    
    public RenderGroup add(Renderable r){
        items.add(new DrawableContainer(r));
        return this;
    }
    
    public List<DrawableContainer> getList(){
        return items;
    }
    
    public void render(){
        items.stream().forEach(item -> {
            item.render();
        });
    }
}
