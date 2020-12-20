/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.SceneRenderUnit;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.*;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class RenderComponent extends RenderGroupComponent implements Renderable {
    private Renderable renderable;
    SceneRenderUnit.UnitProperties unitProperties;
    private float renderDistance = 0f;
    private Matrix4f override;

    public RenderComponent(SceneRenderUnit.UnitProperties unitProperties){
        this(null, unitProperties);
    }

    public RenderComponent(Renderable renderable, SceneRenderUnit.UnitProperties unitProperties){
        this.attachRenderUnit(this, unitProperties);
        this.renderable = renderable;
        this.unitProperties = unitProperties;
    }

    @Override
    public void render() {
        if(isAllEnabled() && renderable != null){
            ShaderController.useConfiguration(unitProperties.shaderPipeline());

            if(override == null)
                CommonUniforms.setPosRotScale(this.getPosition(), this.getRotation(), this.getScale());
            else
                CommonUniforms.setModel(this.override);

            if((renderDistance > 0) && (getPosition().subtract(RenderEngine.getCurrentView().getPosition()).length() > renderDistance))
                return;

            renderable.render();
        }
    }

    public void setOverrideMatrix(Matrix4f mat){
        this.override = mat;
    }

    public RenderComponent setShader(String shader) {
        this.unitProperties = unitProperties.shaderPipeline(shader);
        return this;
    }

    public RenderComponent setFormat(VertexArrayFormat format) {
        this.unitProperties = unitProperties.format(format);
        return this;
    }

    public RenderComponent setTransparency(boolean trans) {
        this.unitProperties = unitProperties.transparency(trans);
        return this;
    }

    public float getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
    }

    public void setRenderable(Renderable renderable){
        this.renderable = renderable;
    }
    
    public Renderable getRenderable(){
        return renderable;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(unitProperties.shaderPipeline());
        out.write(unitProperties.transparency());
        out.write(renderDistance);
        out.write(unitProperties.format().getBindings().size());
        for(var binding : unitProperties.format().getBindings()){
            out.write(binding.bindingIndex());
            out.write(binding.vertexSize());
            out.write(binding.divisor());
            out.write(binding.attributes().size());
            for(var attrib : binding.attributes()){
                out.write(attrib.name());
                out.write(attrib.size());
                out.write(attrib.type().name());
                out.write(attrib.offset());
            }
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        unitProperties = unitProperties.shaderPipeline(in.readString());
        unitProperties = unitProperties.transparency(in.readBoolean());
        renderDistance = in.readFloat();

        unitProperties = unitProperties.format(new VertexArrayFormat(new ArrayList<>()));
        int bindingCount = in.readInt();
        for(int i = 0; i < bindingCount; i++){
            var list = new ArrayList<VertexArrayBinding.VertexArrayAttribute>();
            var index = in.readInt();
            var vertexSize = in.readInt();
            var divisor = in.readInt();
            int attribCount = in.readInt();
            for(int ii = 0; ii < attribCount; ii++){
                var name = in.readString();
                var size = in.readInt();
                var type = in.readString();
                var offset = in.readInt();
                var attrib = new VertexArrayBinding.VertexArrayAttribute(name, size, VertexArrayBinding.VertexArrayAttribute.Type.valueOf(type), offset);
                list.add(attrib);
            }
            var binding = new VertexArrayBinding(index, vertexSize, divisor, list);
            unitProperties.format().addBinding(binding);
        }
    }
}
