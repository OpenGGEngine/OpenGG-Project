/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.Camera;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 *
 * @author Javier
 */
public class ShaderController {
    ShaderProgram program;
    private Shader fragmentTex;
    private Shader vertexTex;
    private Shader geomTex;
    public int uniModel,rotm,lightpos,div,uniView,lightdistance,lightpower,shadow,skycolor,mode,specularexponent,specularexponents,specularcolor,hasspec,uniProj;
    float ratio;
    Matrix4f model= new Matrix4f(), view= new Matrix4f(), proj = new Matrix4f();
    private int uvy;
    private int uvx;
    private int hasnorm;
    private int lightcolor;
    private int posAttrib;
    private int colAttrib;
    private int normAttrib;
    private int texAttrib;
    
    public void setup(URL vert, URL frag, URL geom) throws UnsupportedEncodingException{
        vertexTex= new Shader(GL_VERTEX_SHADER, 
                FileStringLoader.loadStringSequence(
                        URLDecoder.decode(
                                vert.getFile(), "UTF-8"))); 
         
        
        geomTex = new Shader(GL_GEOMETRY_SHADER, 
                FileStringLoader.loadStringSequence(
                        URLDecoder.decode(
                                geom.getFile(), "UTF-8"))); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, 
                FileStringLoader.loadStringSequence(
                        URLDecoder.decode(
                                frag.getFile(), "UTF-8")));
        program = new ShaderProgram();
        
        program.attachShader(vertexTex);   
        program.attachShader(geomTex);
        program.attachShader(fragmentTex); 
        program.link();
        program.use();
        program.checkStatus();
        
       initVertexAttributes();

        /* Set shader variables */
         
        
        uniModel = program.getUniformLocation("model"); 
        program.setUniform(uniModel, new Matrix4f());
        
        uniProj = program.getUniformLocation("projection"); 
        program.setUniform(uniProj, new Matrix4f());
        
        int uniTex = program.getUniformLocation("texImage"); 
        program.setUniform(uniTex, 0);
        
        int uniskycolor = program.getUniformLocation("skycolor"); 
        program.setUniform(uniskycolor, new Vector3f(0.5f,0.5f,0.5f));

        int uniShadow = program.getUniformLocation("shadeImage"); 
        program.setUniform(uniShadow, 1);
        
        int uniCube = program.getUniformLocation("cubemap"); 
        program.setUniform(uniCube, 2);
        
        int uniNorm = program.getUniformLocation("normImage");
        program.setUniform(uniNorm, 3);
        
        int uniSpec = program.getUniformLocation("specImage"); 
        program.setUniform(uniSpec, 4);
        
        lightpos = program.getUniformLocation("light.lightpos"); 
        program.setUniform(lightpos, new Vector3f(200,50,-10));
        
        
        div = program.getUniformLocation("divAmount"); 
        program.setUniform(div, 1f);
        
        uvx = program.getUniformLocation("uvmultx"); 
        program.setUniform(uvx, (float)1f);
        
        uvy = program.getUniformLocation("uvmulty"); 
        program.setUniform(uvy, (float)1f);
        
        rotm = program.getUniformLocation("rot");    
        program.setUniform(rotm, new Vector3f(0,0,0));  

        uniView = program.getUniformLocation("view"); 
        program.setUniform(uniView, new Matrix4f());
        
        shadow = program.getUniformLocation("shmvp"); 
        //program.setUniform(shadow, new Matrix4f());
        
        lightdistance = program.getUniformLocation("light.lightdistance"); 
        program.setUniform(lightdistance, 60f);
        
        lightpower = program.getUniformLocation("light.lightpower"); 
        program.setUniform(lightpower, 300f);
        
        lightcolor = program.getUniformLocation("light.color"); 
        program.setUniform(lightcolor, new Vector3f(1,1,1));
        
        mode = program.getUniformLocation("mode"); 
        program.setUniform(mode, (int) 0);
        
        specularexponent = program.getUniformLocation("material.specexponent");
        program.setUniform(specularexponent, 0);
        
        specularexponents = program.getUniformLocation("material.ka");
        program.setUniform(specularexponents, 1f);
        
        specularcolor = program.getUniformLocation("material.ks");
        program.setUniform(specularcolor, new Vector3f());
        
        hasspec = program.getUniformLocation("material.hasspecmap");
        program.setUniform(hasspec, false);
        
        hasnorm = program.getUniformLocation("material.hasnormmap");
        program.setUniform(hasnorm, false);
        
        program.checkStatus();
        
        GlobalInfo.main = this;
        
        ViewUtil.setPerspective(80, 1280/720, 0.3f, 3000f, program);    
    }
    
    public void initVertexAttributes() {
        //programv.use();
        posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);

        colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 4, 12 * Float.BYTES, 3 * Float.BYTES);
        
        normAttrib = program.getAttributeLocation("normal"); 
        program.enableVertexAttribute(normAttrib);
        program.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        texAttrib = program.getAttributeLocation("texcoord"); 
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);

    }
    
    public void defVertexAttributes(){
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);

        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 4, 12 * Float.BYTES, 3 * Float.BYTES);
        
        program.enableVertexAttribute(normAttrib);
        program.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);

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
            case PP:
                program.setUniform(mode, (int) 5);
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
       
        Vector3f rot = c.getRot();       
  
        view = Matrix4f.rotate(rot.x,1,0,0).multiply(Matrix4f.rotate(rot.y,0,1,0).multiply(Matrix4f.rotate(rot.z,0,0,1))).multiply(Matrix4f.translate(c.getPos()));
        
        setView(view);
    }
    
    public Matrix4f getMVP(){
        return proj.multiply(view).multiply(model);
    }
    
    public void setUVMultX(float f){
        program.setUniform(uvx, (float)f);
    }
    public void setUVMultY(float f){
        program.setUniform(uvy, (float)f);
    }
    public void passMaterial(Material m,boolean specmap, boolean normmap){
        program.setUniform(specularexponent, (float) m.nsExponent);
        program.setUniform(specularexponents, new Vector3f((float)m.ka.rx,(float)m.ka.gy,(float)m.ka.bz));
        program.setUniform(specularcolor, new Vector3f((float)m.ks.rx,(float)m.ks.gy,(float)m.ks.bz));
        program.setUniform(hasspec, specmap);
        program.setUniform(hasnorm, normmap);
    }
}
