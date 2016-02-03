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
import static com.opengg.core.util.GlobalUtil.print;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

/**
 *
 * @author Javier
 */
public class ObjectShader implements ShaderEnabled{
    ShaderProgram program;
    private Shader fragmentTex;
    private Shader vertexTex;
    private int uniModel,rotm,lightpos,div,uniView,lightdistance,lightpower,shadow,skycolor,mode;
    
    private float ratio;
    
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
        
        int uniTex = program.getUniformLocation("texImage"); 
        program.setUniform(uniTex, 0);
        
        int uniskycolor = program.getUniformLocation("skycolor"); 
        program.setUniform(uniskycolor, new Vector3f(0.5f,0.5f,0.5f));
        print(uniskycolor);
        int uniShadow = program.getUniformLocation("shadeImage"); 
        program.setUniform(uniShadow, 2);
        
        lightpos = program.getUniformLocation("lightpos"); 
        program.setUniform(lightpos, new Vector3f(200,50,-10));
        
        div = program.getUniformLocation("divAmount"); 
        program.setUniform(div, 1f);
        
        ratio = win.getRatio();
        
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
        program.use();
        program.setUniform(lightpos, pos);
    }
    @Override
    public void setModel(Matrix4f model){
        program.use();
        program.setUniform(uniModel, model);
    }
    @Override
    public void setView(Matrix4f view){
        program.use();
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
    public void use(){
        program.use();
    }
    @Override
    public void checkError(){
        program.use();
        program.checkStatus();
    }
    
    public void setShadowLightMatrix(Matrix4f m){
        program.use();
        program.setUniform(shadow, m);
    }
}
