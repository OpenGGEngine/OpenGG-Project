/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader.premade;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.render.shader.Shader;
import com.opengg.core.render.shader.ShaderProgram;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.render.window.Window;
import com.opengg.core.world.Camera;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static javax.management.Query.div;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 *
 * @author Javier
 */
public class ObjectShader {
    ShaderProgram program;
    private Shader fragmentTex;
    private Shader vertexTex;
    private int uniModel;
    private int rotm;
    private int lightpos;
    private float ratio;
    private int div;
    public void setup(Window win) throws UnsupportedEncodingException{
        URL verts = ObjectShader.class.getResource("res/sh1.vert");
        URL frags = ObjectShader.class.getResource("res/sh1.frag");
        
        vertexTex= new Shader(GL_VERTEX_SHADER, FileStringLoader.loadStringSequence(URLDecoder.decode(verts.getFile(), "UTF-8"))); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, FileStringLoader.loadStringSequence(URLDecoder.decode(frags.getFile(), "UTF-8"))); 

        
        program = new ShaderProgram();
        program.attachShader(vertexTex);   
        program.attachShader(fragmentTex);  
        program.link();
        program.use();
        program.checkStatus();
        
        specifyVertexAttributes(program, true);

        /* Set shader variables */
        program.use(); uniModel = program.getUniformLocation("model");
        
        int uniTex = program.getUniformLocation("texImage"); program.setUniform(uniTex, 0);
        
        rotm = program.getUniformLocation("rot"); program.setUniform(rotm, new Vector3f(0,0,0));
        
        lightpos = program.getUniformLocation("lightpos"); program.setUniform(lightpos, new Vector3f(200,50,-10));
        
        div = program.getUniformLocation("divAmount"); program.setUniform(div, 1f);
        
        ratio = win.getRatio();
        
        program.use();
        ViewUtil.setPerspective(80, ratio, 0.3f, 3000f, program);    
    }
    
    private void specifyVertexAttributes(ShaderProgram programv, boolean textured) {
        programv.use();
        int posAttrib = programv.getAttributeLocation("position");
        programv.enableVertexAttribute(posAttrib);
        programv.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);

        int colAttrib = programv.getAttributeLocation("color");
        programv.enableVertexAttribute(colAttrib);
        programv.pointVertexAttribute(colAttrib, 4, 12 * Float.BYTES, 3 * Float.BYTES);
        
        int normAttrib = programv.getAttributeLocation("normal"); 
        programv.enableVertexAttribute(normAttrib);
        programv.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        int texAttrib = programv.getAttributeLocation("texcoord"); 
        programv.enableVertexAttribute(texAttrib);
        programv.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);

    }
    public void setLightPos(Vector3f pos){
        program.setUniform(lightpos, pos);
    }
    public void setModel(Matrix4f model){
        program.setUniform(uniModel, model);
    }
    public ShaderProgram getProgram(){
        return program;
    }
}
