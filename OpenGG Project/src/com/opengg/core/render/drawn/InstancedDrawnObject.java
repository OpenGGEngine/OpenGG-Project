/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.engine.RenderEngine;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.GLBuffer;
import com.opengg.core.render.shader.ShaderController;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL31.glDrawElementsInstanced;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class InstancedDrawnObject extends DrawnObject implements Drawable {
    GLBuffer ivbo;
    long offset;
    int instnum;
    Matrix4f shadeModel = new Matrix4f();
    
    public InstancedDrawnObject(FloatBuffer b){
        super(b);
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(3);
            buffer.put(0).put(0).put(0);
            
            defInstanceBuffer(buffer);
        }
    }
    
    public InstancedDrawnObject(FloatBuffer b, IntBuffer i){
        super(b,i);
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(3);
            buffer.put(0).put(0).put(0);
            
            defInstanceBuffer(buffer);
        }
    }
    
    public InstancedDrawnObject(FloatBuffer b, FloatBuffer inst){
        super(b);

        instnum = inst.limit()/4;
        
        defInstanceBuffer(inst);
    }
    
    public InstancedDrawnObject(List<FloatBuffer> buffers, FloatBuffer inst){
        super(buffers);

        instnum = inst.limit()/4;
        
        defInstanceBuffer(inst);
    }
    
    public InstancedDrawnObject(FloatBuffer b, IntBuffer index, FloatBuffer inst){
        super(b, index);

        instnum = inst.limit()/4;
        
        defInstanceBuffer(inst);
    }

    public void defInstanceBuffer(FloatBuffer data){
        ivbo = new GLBuffer(GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
        ivbo.bind();
        ivbo.uploadData(data);
    }
    
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }

    @Override
    public void render(){    
        ShaderController.setModel(model);
        RenderEngine.getCurrentVAO().applyFormat(vbo, ivbo);
        evbo.bind();
        glDrawElementsInstanced(adj ? GL_TRIANGLES_ADJACENCY : GL_TRIANGLES, ind.limit(), GL_UNSIGNED_INT, 0, instnum);
    }
    
    public void removeBuffer(){
        vertices = null;
        
    }

    @Override
    public void destroy() {
        removeBuffer();
    }
    public void setPositions(FloatBuffer f, int instancenum){
        ivbo.bind();
        ivbo.uploadData(f);
        instnum = instancenum;
    }
}
