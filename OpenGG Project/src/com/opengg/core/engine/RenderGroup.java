/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.Mode;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderGroup {
    List<Renderable> items = new ArrayList<>();
    
    boolean transparency = false;
    boolean distancefield;
    boolean shadows = false;
    boolean adj = false;
    int order = 5;
    Mode mode = Mode.OBJECT;

    public RenderGroup(){
        RenderEngine.sortOrders();
    }
    
    public boolean isText() {
        return distancefield;
    }

    public void setText(boolean distancefield) {
        this.distancefield = distancefield;
    }
  
    public boolean isTransparent() {
        return transparency;
    }

    public void setTransparent(boolean transparency) {
        this.transparency = transparency;
    }

    public boolean hasAdjacencyMesh(){
        return adj;
    }
    
    public void setAdjacencyMesh(boolean adj){
        this.adj = adj;
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
        RenderEngine.sortOrders();
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public RenderGroup add(Renderable r){
        items.add(r);
        return this;
    }
    
    public List<Renderable> getList(){
        return items;
    }
    
    public void render(){
        items.stream().forEach(item -> {
            item.render();
        });
    }
}
