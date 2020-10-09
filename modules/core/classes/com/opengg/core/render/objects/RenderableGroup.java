/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.objects;

import com.opengg.core.render.Renderable;

import java.util.List;

/**
 * Groups a set of Renderables into a single render operation
 * @author Javier
 */
public class RenderableGroup implements Renderable {
    public List<Renderable> objs;

    public static RenderableGroup of(Renderable... obs){
        return new RenderableGroup(List.of(obs));
    }

    public RenderableGroup(List<Renderable> objs) {
        this.objs = List.copyOf(objs);
    }
    
    public void add(Renderable renderable){
        objs.add(renderable);
    }

    public void remove(Renderable d){
        objs.remove(d);
    }

    @Override
    public void render() {
        for(var renderable : objs){
            renderable.render();
        }
    }
}
