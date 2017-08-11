/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.shader.VertexArrayAttribute;
import com.opengg.core.render.shader.VertexArrayFormat;
import com.opengg.core.util.GGByteInputStream;
import com.opengg.core.util.GGByteOutputStream;
import java.io.IOException;

/**
 *
 * @author Javier
 */
public class RenderComponent extends Component implements Renderable{
    Drawable g;
    public Matrix4f m = new Matrix4f();
    String shader;
    VertexArrayFormat format;
    boolean transparent;
    float renderDistance = 0f;
    
    public RenderComponent(){
        super();
        format = RenderEngine.getDefaultFormat();
        shader = "object";
    };
    
    public RenderComponent(Drawable g){
        this();
        this.g = g;
    }

    @Override
    public void render() {
        m = new Matrix4f().translate(getPosition()).rotateQuat(getRotation()).scale(getScale());
        if((renderDistance != 0) && (getPosition().subtract(RenderEngine.getCurrentCamera().getPos()).length() > renderDistance))
            return;
        if(g != null){
            g.setMatrix(m);
            g.render();
        }
    }

    @Override
    public void update(float delta){
        
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
        this.g = d;
    }
    
    public Drawable getDrawable(){
        return g;
    }
    
    @Override
    public void serialize(GGByteOutputStream out) throws IOException{
        super.serialize(out);
        out.write(shader);
        out.write(transparent);
        out.write((float)renderDistance);
        out.write(format.getAttributes().size());
        for(VertexArrayAttribute attrib : format.getAttributes()){
            out.write(attrib.arrayindex);
            out.write(attrib.divisor);
            out.write(attrib.name);
            out.write(attrib.offset);
            out.write(attrib.size);
            out.write(attrib.buflength);
            out.write(attrib.type);
        }
    }
    
    @Override
    public void deserialize(GGByteInputStream in) throws IOException{
        super.deserialize(in);
        shader = in.readString();
        transparent = in.readBoolean();
        renderDistance = in.readFloat();
        int attlength = in.readInt();
        format = new VertexArrayFormat();
        for(int i = 0; i < attlength; i++){
            int index = in.readInt();
            boolean divisor = in.readBoolean();
            String name = in.readString();
            int offset = in.readInt();
            int size = in.readInt();
            int buflength = in.readInt();
            int type = in.readInt();
            VertexArrayAttribute attrib = new VertexArrayAttribute(name, size, buflength, type, offset, index, divisor);
            format.addAttribute(attrib);
        }
    }
}
