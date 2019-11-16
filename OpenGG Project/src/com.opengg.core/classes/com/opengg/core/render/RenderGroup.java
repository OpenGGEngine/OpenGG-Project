/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render;

import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.render.shader.VertexArrayObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Javier
 */
public class RenderGroup {
    private List<Renderable> items = new ArrayList<>();
    private VertexArrayObject vao;
    private String pipeline = "object";
    private String name = "default";
    private boolean transparency = false;
    private boolean enabled = true;
    private int order = 5;

    public RenderGroup(String name){
        this.name = name;
        vao = new VertexArrayObject(RenderEngine.getDefaultFormat());
    }
    
    public RenderGroup(String name, VertexArrayFormat format){
        this.name = name;
        vao = new VertexArrayObject(format);
    }

    public RenderGroup(String name, VertexArrayFormat format, String pipeline) {
        this.pipeline = pipeline;
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

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    public RenderGroup add(Renderable r){
        if(!items.contains(r))
            items.add(r);
        return this;
    }
    
    public List<Renderable> getList(){
        return Collections.unmodifiableList(items);
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

    @Override
    public String toString() {
        return "RenderGroup{" +
                "pipeline='" + pipeline + '\'' +
                ", name='" + name + '\'' +
                ", enabled=" + enabled +
                ", item count=" + items.size() +
                '}';
    }
}
