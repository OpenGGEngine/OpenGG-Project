/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.world.Camera;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 *
 * @author Javier
 */
public class ShaderController {
    ShaderProgram program;
    private Shader fragmentTex;
    private Shader vertexTex;
    private int uniModel,rotm,lightpos,div,uniView,lightdistance,lightpower,shadow,skycolor,mode;
    float ratio;
    Matrix4f model= new Matrix4f(), view= new Matrix4f(), proj = new Matrix4f();
    
    public void setup(URL vert, URL frag) throws UnsupportedEncodingException{
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
         
        
        uniModel = program.getUniformLocation("model"); 
        program.setUniform(uniModel, new Matrix4f());
        
        int uniTex = program.getUniformLocation("texImage"); 
        program.setUniform(uniTex, 0);
        
        int uniskycolor = program.getUniformLocation("skycolor"); 
        program.setUniform(uniskycolor, new Vector3f(0.5f,0.5f,0.5f));

        int uniShadow = program.getUniformLocation("shadeImage"); 
        program.setUniform(uniShadow, 1);
        
        int uniCube = program.getUniformLocation("cubemap"); 
        program.setUniform(uniCube, 2);
        
        lightpos = program.getUniformLocation("lightpos"); 
        program.setUniform(lightpos, new Vector3f(200,50,-10));
        
        div = program.getUniformLocation("divAmount"); 
        program.setUniform(div, 1f);
        

        
        rotm = program.getUniformLocation("rot");    
        program.setUniform(rotm, new Vector3f(0,0,0));  

        uniView = program.getUniformLocation("view"); 
        program.setUniform(uniView, new Matrix4f());
        
        shadow = program.getUniformLocation("shmvp"); 
        //program.setUniform(shadow, new Matrix4f());
        
        lightdistance = program.getUniformLocation("lightdistance"); 
        program.setUniform(lightdistance, 5f);
        
        lightpower = program.getUniformLocation("lightpower"); 
        program.setUniform(lightpower, 200f);
        
        mode = program.getUniformLocation("mode"); 
        program.setUniform(mode, (int) 0);
        
        program.checkStatus();
        
        ViewUtil.setPerspective(80, 1280/720, 0.3f, 3000f, program);    
    }
    
    private void specifyVertexAttributes(ShaderProgram programv, boolean textured) {
        //programv.use();
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
    
    public void setView(Matrix4f view){
        
        program.setUniform(uniView, view);
    }
    
    
    public void setPerspective(float fov, float aspect, float znear, float zfar){
        
        ViewUtil.setPerspective(fov, aspect, znear, zfar, program);
    }
    
    public void setOrtho(float left, float right, float bottom, float top, float near, float far){
        
        ViewUtil.setOrtho(left, right, bottom, top, near, far, program);
    }
    
    public void setFrustum(float left, float right, float bottom, float top, float near, float far){
        
        ViewUtil.setFrustum(left, right, bottom, top, near, far, program);
    }
    
    public ShaderProgram getProgram(){
        
        return program;
    }
    
    public void setMode(Mode m){
        switch(m){
            case OBJECT:
                program.setUniform(mode, (int) 0);
                break;
            case OBJECT_NO_SHADOW:
                program.setUniform(mode, (int) 1);
                break;
            case GUI:
                program.setUniform(mode, (int) 2);
                break;
            case SKYBOX:
                program.setUniform(mode, (int) 3);
                break;
            case POS_ONLY:
                program.setUniform(mode, (int) 4);
                break;
        }
    }
    
    public void checkError(){
        
        program.checkStatus();
    }
    
    public void setShadowLightMatrix(Matrix4f m){
        
        program.setUniform(shadow, m);
    }
    
    public void setView(Camera c){
        Vector3f pos = c.getPos();
        Vector3f rot = c.getRot();       
        Matrix4f posm = Matrix4f.translate(pos.x, pos.y, pos.z);
        
        
        Matrix4f rotm = Matrix4f.rotate(rot.x,1,0,0).multiply(Matrix4f.rotate(rot.y,0,1,0).multiply(Matrix4f.rotate(rot.z,0,0,1)));
        
        view = rotm.multiply(posm);
        
        setView(view);
    }
    
    public Matrix4f getMVP(){
        return proj.multiply(view).multiply(model);
    }
}
