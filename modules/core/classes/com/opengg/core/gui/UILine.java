package com.opengg.core.gui;

import com.opengg.core.math.Vector2f;
import com.opengg.core.render.objects.DrawnObject;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.system.Allocator;

import java.util.List;
import java.util.stream.IntStream;

public class UILine extends UIRenderable {
    Texture tex;
    public UILine() {
    }

    public UILine(Vector2f pos){
        this.setPositionOffset(pos);
    }

    public UILine(Texture tex) {
        this.tex = tex;
    }

    public UILine(Vector2f pos, List<Vector2f> points){
        this.setPositionOffset(pos);
        setPoints(points);
    }

    public UILine(Vector2f pos, List<Vector2f> points, Texture texture){
        this.setPositionOffset(pos);
        setPoints(points);
        this.tex = texture;
    }

    public void setPoints(List<Vector2f> points){
        var buffer = Allocator.allocFloat(points.size()*8);
        var instancebuffer = Allocator.allocInt(points.size()*2);
        for(var p : points){
            buffer.put(p.x).put(p.y).put(0);
            buffer.put(new float[]{0,0,0,0,0});
        }
        IntStream.range(0,points.size()-1)
                .forEach(i -> instancebuffer.put(i).put(i+1));
        buffer.flip();
        instancebuffer.flip();
        var drawable = DrawnObject.create(instancebuffer,buffer);
        drawable.setRenderType(DrawnObject.DrawType.LINES);
        this.setRenderable(drawable);
    }

    @Override
    public void render(){
        if(tex != null) ShaderController.setUniform("Kd", tex);
        super.render();
    }
}
