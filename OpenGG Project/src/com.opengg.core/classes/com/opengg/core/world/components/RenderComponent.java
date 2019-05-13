/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.math.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayBinding;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.util.StreamUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class RenderComponent extends Component implements Renderable{
    private Drawable drawable;
    private String shader;
    private VertexArrayFormat format;
    private boolean transparent;
    private boolean render = true;
    private float renderDistance = 0f;
    private Matrix4f override;
    
    public RenderComponent(){
        super();
        format = RenderEngine.getDefaultFormat();
        shader = "object";
    }

    public RenderComponent(Drawable drawable){
        this();
        this.drawable = drawable;
    }

    @Override
    public void render() {
        Matrix4f matrix;
        if(override == null)
            matrix = new Matrix4f().translate(getPosition()).rotate(getRotation()).scale(getScale());
        else
            matrix = override;

        if((renderDistance > 0) && (getPosition().subtract(RenderEngine.getCurrentView().getPosition()).length() > renderDistance)) {
            return;
        }

        if(!render) return;

        if(drawable != null){
            drawable.setMatrix(matrix);
            drawable.render();
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
    
    public void setDrawable(Drawable d){
        this.drawable = d;
    }
    
    public Drawable getDrawable(){
        return drawable;
    }

    @Override
    public void onWorldChange(){
        this.getWorld().addRenderable(this);
    }

    public boolean shouldRender(){
        return render;
    }

    public void setShouldRender(boolean render){
        this.render = render;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException{
        super.serialize(out);
        out.write(shader);
        out.write(transparent);
        out.write(renderDistance);
        out.write(format.getBindings().size());
        for(var binding : format.getBindings()){
            for(var attrib : binding.getAttributes()){
                out.write(binding.getBindingIndex());
                out.write(binding.getDivisor());
                out.write(attrib.name);
                out.write(attrib.offset);
                out.write(attrib.size);
                out.write(binding.getVertexSize());
                out.write(attrib.type);
            }
        }
    }
    
    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        shader = in.readString();
        transparent = in.readBoolean();
        renderDistance = in.readFloat();
        int attlength = in.readInt();
        format = new VertexArrayFormat();
        var tempMap = new HashMap<Integer, Tuple<Tuple<Integer, Integer>, List<VertexArrayAttribute>>>();
        for(int i = 0; i < attlength; i++){
            int index = in.readInt();
            boolean divisor = in.readBoolean();
            String name = in.readString();
            int offset = in.readInt();
            int size = in.readInt();
            int buflength = in.readInt();
            int type = in.readInt();
            VertexArrayAttribute attrib = new VertexArrayAttribute(name, size, type, offset);
            tempMap.merge(index, Tuple.of(Tuple.of(divisor ? 1 : 0, buflength), List.of(attrib)), (x,y) -> {
                var newList = Stream.concat(x.y.stream(), y.y.stream()).collect(Collectors.toList());
                return Tuple.of(x.x, newList);
            });
        }

        tempMap.entrySet().stream()
                .map(c -> new VertexArrayBinding(c.getKey(), c.getValue().x.y, c.getValue().x.x, c.getValue().y))
                .forEach(format::addBinding);
    }
}
