/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderGroup {
    List<Renderable> items = new ArrayList<>();
    VertexArrayObject vao;
    String pipeline = "object";
    String name = "default";
    boolean transparency = false;
    boolean shadows = false;
    boolean enabled = true;
    int order = 5;

    public RenderGroup(String name){
        this.name = name;
        vao = new VertexArrayObject(RenderEngine.getDefaultFormat());
    }
    
    public RenderGroup(String name, VertexArrayFormat format){
        this.name = name;
        vao = new VertexArrayObject(format);
    }
  
    public VertexArrayFormat getFormat(){
        return vao.getFormat();
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
    
    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }
    
    public RenderGroup add(Renderable r){
        if(!items.contains(r))
            items.add(r);
        return this;
    }
    
    public List<Renderable> getList(){
        return items;
    }
    
    public void render(){
        vao.bind();
        for(Renderable r : items) r.render(); 
        vao.unbind();
    }
    
    public void clear(){
        items.clear();
    }
    
    public void remove(Renderable r){
        items.remove(r);
    }
}
