/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.Renderable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderGroup {
    List<Renderable> items = new ArrayList<>();
    
    String pipeline = "object";
    boolean transparency = false;
    boolean shadows = false;
    int order = 5;

    public RenderGroup(){
        RenderEngine.sortOrders();
    }
  
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
        RenderEngine.sortOrders();
    }

    public String getPipeline() {
        return pipeline;
    }

    public void setPipeline(String pipeline) {
        this.pipeline = pipeline;
    }
    
    public RenderGroup add(Renderable r){
        items.add(r);
        return this;
    }
    
    public List<Renderable> getList(){
        return items;
    }
    
    public void render(){
        items.stream().forEach(Renderable::render);
    }
    
    public void clear(){
        items.clear();
    }
    
    public void remove(Renderable r){
        items.remove(r);
    }
}
