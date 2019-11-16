/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayBinding;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class RenderComponent extends Component implements com.opengg.core.render.Renderable {
    private Renderable renderable;
    private String shader;
    private VertexArrayFormat format;
    private boolean transparent;
    private float renderDistance = 0f;
    private Matrix4f override;
    
    public RenderComponent(){
        super();
        format = RenderEngine.getDefaultFormat();
        shader = "object";
    }

    public RenderComponent(Renderable renderable){
        this();
        this.renderable = renderable;
    }

    @Override
    public void render() {
        if(override == null)
            ShaderController.setPosRotScale(this.getPosition(), this.getRotation(), this.getScale());
        else
            ShaderController.setModel(this.override);

        if((renderDistance > 0) && (getPosition().subtract(RenderEngine.getCurrentView().getPosition()).length() > renderDistance)) {
            return;
        }

        if(isEnabled() && renderable != null){
            renderable.render();
        }
    }

    public void setOverrideMatrix(Matrix4f mat){
        this.override = mat;
    }

    public String getShader() {
        return shader;
    }

    public void setShader(String shader) {
        this.shader = shader;
    }

    public VertexArrayFormat getFormat() {
        return format;
    }

    public void setFormat(VertexArrayFormat format) {
        this.format = format;
    }
    
    public boolean isTransparent() {
        return transparent;
    }

    public void setTransparency(boolean trans) {
        this.transparent = trans;
    }

    public float getRenderDistance() {
        return renderDistance;
    }

    public void setRenderDistance(float renderDistance) {
        this.renderDistance = renderDistance;
    }

    public void setRenderable(Renderable d){
        this.renderable = d;
    }
    
    public Renderable getRenderable(){
        return renderable;
    }

    @Override
    public void finalizeComponent(){
        if(this.getWorld() != null)
            this.getWorld().removeRenderable((RenderComponent)this);
    }

    @Override
    public void onWorldChange(){
        this.getWorld().addRenderable(this);
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(shader);
        out.write(transparent);
        out.write(renderDistance);
        out.write(format.getBindings().size());
        for(var binding : format.getBindings()){
            out.write(binding.getBindingIndex());
            out.write(binding.getVertexSize());
            out.write(binding.getDivisor());
            out.write(binding.getAttributes().size());
            for(var attrib : binding.getAttributes()){
                out.write(attrib.name);
                out.write(attrib.size);
                out.write(attrib.type);
                out.write(attrib.offset);
            }
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        shader = in.readString();
        transparent = in.readBoolean();
        renderDistance = in.readFloat();

        format = new VertexArrayFormat();
        int bindingCount = in.readInt();
        for(int i = 0; i < bindingCount; i++){
            var list = new ArrayList<VertexArrayAttribute>();
            var index = in.readInt();
            var vertexSize = in.readInt();
            var divisor = in.readInt();
            int attribCount = in.readInt();
            for(int ii = 0; ii < attribCount; ii++){
                var name = in.readString();
                var size = in.readInt();
                var type = in.readInt();
                var offset = in.readInt();
                var attrib = new VertexArrayAttribute(name, size, type, offset);
                list.add(attrib);
            }
            var binding = new VertexArrayBinding(index, vertexSize, divisor, list);
            format.addBinding(binding);
        }
    }
}
