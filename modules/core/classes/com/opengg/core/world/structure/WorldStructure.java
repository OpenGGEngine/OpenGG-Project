/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.structure;

import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.RenderGroup;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Javier
 */
public class WorldStructure {
    private List<WorldGeometry> allGeometry = new ArrayList();
    private List<RenderGroup> renderGroups = new ArrayList<>();
    private World parent;

    public WorldStructure(World parent){
        this.parent = parent;
    }

    public void addGeometry(WorldGeometry geometry){
        allGeometry.add(geometry);
        geometry.setParent(this);
    }

    public void removeGeometry(WorldGeometry geometry){
        allGeometry.remove(geometry);
    }

    public List<WorldGeometry> getAllGeometry(){
        return allGeometry;
    }

    public Optional<WorldGeometry> getByGUID(long guid) {
        return allGeometry.stream()
                .filter(g -> g.getGuid() == guid)
                .findFirst();
    }

    public void remakeRenderGroups(){
        if(renderGroups.isEmpty()){
            renderGroups.add(new RenderGroup("world structure models", RenderEngine.getTangentVAOFormat(), "tangent"));
            renderGroups.add(new RenderGroup("world structure cuboids", RenderEngine.getDefaultFormat(), "cuboid"));
            renderGroups.forEach(r -> r.setEnabled(true));
            renderGroups.forEach(parent.getRenderEnvironment()::addGroup);
        }

        for(var geom : allGeometry){
            if(geom.getRenderable() == null) continue;
            switch (geom.getClass().getSimpleName()){
                case "CuboidWorldGeometry" -> renderGroups.get(1).add(geom);
                case "ModelWorldGeometry" -> renderGroups.get(0).add(geom);
            }
        }
    }

    public List<RenderGroup> getRenderGroups() {
        return renderGroups;
    }

    public World getParent() {
        return parent;
    }

    public void serialize(GGOutputStream out) throws IOException {
        out.write(allGeometry.size());
        for(var geom : allGeometry){
            out.write(geom.getClass().getName());
            geom.localSerialize(out);
        }
    }

    public void deserialize(byte[] bytes) throws IOException {
        var in = new GGInputStream(bytes);
        var amount = in.readInt();
        for(int i = 0; i < amount; i++){
            try {
                var geom = (WorldGeometry) ClassUtil.createByName(in.readString());
                geom.localDeserialize(in);
                addGeometry(geom );
            } catch (ClassInstantiationException e) {
                e.printStackTrace();
            }
        }
    }
}
