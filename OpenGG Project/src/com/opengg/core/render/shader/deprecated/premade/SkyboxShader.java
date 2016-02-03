/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader.deprecated.premade;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.render.shader.Shader;
import com.opengg.core.render.shader.ShaderProgram;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.render.window.Window;
import com.opengg.core.util.Time;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 *
 * @author Javier
 */
public class SkyboxShader implements ShaderEnabled{
    ShaderProgram program;
    private Shader fragmentTex;
    private Shader vertexTex;
    private int uniModel;
    private int rotm;
    private int lightpos;
    private float ratio;
    private int div;
    private int uniView;
    private int lightdistance;
    private int lightpower;
    private float rotation;
    private final float rotatespeed = 1f;
            
    public void setup(Window win, URL vert, URL frag) throws UnsupportedEncodingException{
        vertexTex= new Shader(GL_VERTEX_SHADER, 
                FileStringLoader.loadStringSequence(
                        URLDecoder.decode(
                                vert.getFile(), "UTF-8"))); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, 
                FileStringLoader.loadStringSequence(
                        URLDecoder.decode(
                                frag.getFile(), "UTF-8"))); 
        
        
        program = new ShaderProgram();
        
        program.attachShader(vertexTex);   
        program.attachShader(fragmentTex);  
        
        program.link();
        program.use();
        program.checkStatus();
        
        specifyVertexAttributes(program, true);

        /* Set shader variables */
        program.use(); 
        uniModel = program.getUniformLocation("model"); 
        program.setUniform(uniModel, new Matrix4f());
        
        int uniTex = program.getUniformLocation("skyTex"); 
        program.setUniform(uniTex, 2);
        
        int skyboxSize = program.getUniformLocation("skyboxSize");
        program.setUniform(skyboxSize, 1f);
        
        ratio = win.getRatio();
           
        uniView = program.getUniformLocation("view"); 
        program.setUniform(uniView, new Matrix4f());       
        
        program.checkStatus();
        
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
    @Override
    public void setLightPos(Vector3f pos){
        
    }
    @Override
    public void setModel(Matrix4f model){
        program.use();
         
        program.setUniform(uniModel, model);
    }
    @Override
    public void setView(Matrix4f view){
        program.use();
        
         view.m03 = 0;
        view.m13 = 175;
        view.m23 = 0;
       
        //Matrix4f.rotate(rotation, 0, 1, 0);
      
        program.setUniform(uniView, view);
    }
    
    @Override
    public void setProjection(float fov, float aspect, float znear, float zfar){
        program.use();
        ViewUtil.setPerspective(fov, aspect, znear, zfar, program);
    }
    @Override
    public void setOrtho(float left, float right, float bottom, float top, float near, float far){
        program.use();
        ViewUtil.setOrtho(left, right, bottom, top, near, far, program);
    }
    @Override
    public void setFrustum(float left, float right, float bottom, float top, float near, float far){
        program.use();
        ViewUtil.setFrustum(left, right, bottom, top, near, far, program);
    }
    @Override
    public ShaderProgram getProgram(){
        program.use();
        return program;
    }
    @Override
    public void use(){//specifyVertexAttributes(program, true);
        program.use();
    }
    @Override
    public void checkError(){
        program.use();
        program.checkStatus();
    }
}
