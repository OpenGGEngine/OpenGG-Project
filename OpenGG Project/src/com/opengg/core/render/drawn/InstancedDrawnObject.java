/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.drawn;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.shader.ShaderController;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL31.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL32.GL_TRIANGLES_ADJACENCY;
import org.lwjgl.system.MemoryStack;

/**
 *
 * @author Javier
 */
public class InstancedDrawnObject extends DrawnObject implements Drawable {
    VertexBufferObject ivbo;
    long offset;
    int instnum;
    Matrix4f shadeModel = new Matrix4f();
    
    static{
        DrawnObjectHandler.setup();
    }
    
    public InstancedDrawnObject(FloatBuffer b){
        super(b);
        try(MemoryStack stack = MemoryStack.stackPush()){
            FloatBuffer buffer = stack.callocFloat(3);
            buffer.put(0).put(0).put(0);
            
            ivbo = new VertexBufferObject();
            ivbo.bind(GL_ARRAY_BUFFER);
            ivbo.uploadData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        }
    }
    
    public InstancedDrawnObject(FloatBuffer b, FloatBuffer inst){
        super(b);

        instnum = inst.limit()/4;
        
        ivbo = new VertexBufferObject();
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, inst, GL_STATIC_DRAW);
    }
    
    public InstancedDrawnObject(List<FloatBuffer> buffers, FloatBuffer inst){
        super(buffers);

        instnum = inst.limit()/4;
        
        ivbo = new VertexBufferObject();
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, inst, GL_STATIC_DRAW);
    }
    
    public InstancedDrawnObject(FloatBuffer b, IntBuffer index, FloatBuffer inst){
        super(b, index);

        instnum = inst.limit()/4;
        
        ivbo = new VertexBufferObject();
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(adj ? GL_TRIANGLES_ADJACENCY : GL_TRIANGLES, inst, GL_STATIC_DRAW);
    }
  
    @Override
    public void setMatrix(Matrix4f model){
        this.model = model;
    }

    @Override
    public void draw(){    
        ShaderController.setModel(model);       
        vbo.bind(GL_ARRAY_BUFFER);
        evbo.bind(GL_ELEMENT_ARRAY_BUFFER);
        ShaderController.defInstancedVertexAttributes(ivbo);
        ShaderController.setInstanced(true);
        glDrawArraysInstanced(GL_TRIANGLES,0,ind.limit(), instnum);
        ShaderController.setInstanced(false);
    }
    
    public void removeBuffer(){
        b = null;
        
    }

    @Override
    public Matrix4f getMatrix() {
        return model;
    }

    @Override
    public void destroy() {
        removeBuffer();
    }
    public void setPositions(FloatBuffer f,int numparticles){
        ivbo.bind(GL_ARRAY_BUFFER);
        ivbo.uploadData(GL_ARRAY_BUFFER, f, GL_STATIC_DRAW);
        instnum = numparticles;
    }
}
