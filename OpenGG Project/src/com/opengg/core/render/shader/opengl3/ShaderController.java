/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader.opengl3;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.Light;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.world.Camera;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_SHADER;

/**
 *
 * @author Javier
 */
public class ShaderController {
    private static ShaderProgram program;
    private static Shader fragmentTex;
    private static Shader vertexTex;
    private static Shader geomTex;
    private static float ratio;
    private static Matrix4f model= new Matrix4f(), view= new Matrix4f(), proj = new Matrix4f();
    private static int posAttrib,colAttrib,normAttrib,texAttrib;
    private static int uniModel,rotm,lightpos,div,uniView,
            lightdistance,lightpower,shadow,skycolor,
            mode,specularcolor,hasspec,uniProj,billboard,
            ambient,hasspecpow,hasamb,specularpow, uvy,
            uvx,hasnorm,lightcolor,inst,text,time;
    
    public static void initialize(URL vert, URL frag, URL geom){
        try {
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
            GGConsole.log("Shader files loaded and validated");
        } catch (UnsupportedEncodingException ex) {
            GGConsole.error("Unable to parse shader files!");
            return;
        }
        program.attachShader(vertexTex);
        program.attachShader(geomTex);
        program.attachShader(fragmentTex);
        program.link();
        program.use();
        program.checkStatus();

        initVertexAttributes();
        
        GGConsole.log("Shaders linked, attributes have been validated");

        /* Set shader variables */

        inst = program.getUniformLocation("inst");
        program.setUniform(inst, 0);

        text = program.getUniformLocation("text");
        program.setUniform(text, false);

        uniModel = program.getUniformLocation("model");
        program.setUniform(uniModel, new Matrix4f());

        uniProj = program.getUniformLocation("projection");
        program.setUniform(uniProj, new Matrix4f());

        int uniCube = program.getUniformLocation("cubemap");
        program.setUniform(uniCube, 2);

        int uniskycolor = program.getUniformLocation("skycolor");
        program.setUniform(uniskycolor, new Vector3f(0.5f,0.5f,0.5f));

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

        lightdistance = program.getUniformLocation("light.lightdistance");
        program.setUniform(lightdistance, 250f);

        lightpower = program.getUniformLocation("light.lightpower");
        program.setUniform(lightpower, 100f);

        lightcolor = program.getUniformLocation("light.color");
        program.setUniform(lightcolor, new Vector3f(1,1,1));

        mode = program.getUniformLocation("mode");
        program.setUniform(mode, (int) 0);

        time = program.getUniformLocation("time");
        program.setUniform(time, 0f);

        setMatLinks();

        billboard = program.getUniformLocation("billboard");
        program.setUniform(billboard,false);
        program.checkStatus();

        ViewUtil.setPerspective(80, OpenGG.window.getRatio(), 0.3f, 3000f, program);
        
    }
    
    private static void setMatLinks(){
        
        int uniTex = program.getUniformLocation("Kd"); 
        program.setUniform(uniTex, 0);
        
        int uniAmb = program.getUniformLocation("Ka");
        program.setUniform(uniAmb, 1);
        
        int uniNorm = program.getUniformLocation("bump");
        program.setUniform(uniNorm, 3);
        
        int uniSpec = program.getUniformLocation("Ks"); 
        program.setUniform(uniSpec, 4);
        
        int uniExp = program.getUniformLocation("Ns"); 
        program.setUniform(uniExp, 5);
        
        ambient = program.getUniformLocation("material.ka");
        program.setUniform(ambient, 1f);
        
        specularcolor = program.getUniformLocation("material.ks");
        program.setUniform(specularcolor, new Vector3f());
        
        specularpow = program.getUniformLocation("material.ns");
        program.setUniform(specularpow, 0f);
        
        hasspec = program.getUniformLocation("material.hasspecmap");
        program.setUniform(hasspec, false);
        
        hasnorm = program.getUniformLocation("material.hasnormmap");
        program.setUniform(hasnorm, false);
        
        hasamb= program.getUniformLocation("material.hasambmap");
        program.setUniform(hasamb, false);
        
        hasspecpow = program.getUniformLocation("material.hasspecpow");
        program.setUniform(hasspecpow, false);
    }
    
    public static void initVertexAttributes() {
        posAttrib = program.getAttributeLocation("position");
        colAttrib = program.getAttributeLocation("color");      
        normAttrib = program.getAttributeLocation("normal");       
        texAttrib = program.getAttributeLocation("texcoord"); 

    }
    
    public static void defVertexAttributes(){
        program.enableVertexAttribute(posAttrib);
        program.enableVertexAttribute(colAttrib);      
        program.enableVertexAttribute(normAttrib); 
        program.enableVertexAttribute(texAttrib);   
        program.setVertexAttribDivisor(colAttrib, 0);
    }
    
    public static void pointVertexAttributes(){
        program.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);
        program.pointVertexAttribute(colAttrib, 4, 12 * Float.BYTES, 3 * Float.BYTES);
        program.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        program.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);     
    }
    
    public static void defInstancedVertexAttributes(VertexBufferObject b){
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);

        program.enableVertexAttribute(normAttrib);
        program.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        program.enableVertexAttribute(texAttrib);
        program.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);
        
        b.bind(GL_ARRAY_BUFFER);
        
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 4, 3 * Float.BYTES, 0);
        program.setVertexAttribDivisor(colAttrib, 1);

    }
    
    public static void setLightPos(Vector3f pos){
        
        program.setUniform(lightpos, pos);
    }
    
    public static void setModel(Matrix4f model){
        
        program.setUniform(uniModel, model);
    }
    
    public static void setTimeMod(float mod){
        
        program.setUniform(time, mod);
    }
    
    public static void setView(Matrix4f view){
        
        program.setUniform(uniView, view);
    }
    
    public static void setDistanceField(boolean distfield){
        
        program.setUniform(text, distfield);
    }
    
    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        proj = Matrix4f.perspective(fov, aspect, znear, zfar);
        program.setUniform(uniProj, proj);
    }
    
    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.orthographic(left, right, bottom, top, near, far);
        program.setUniform(uniProj, proj);
    }
    
    public static void setFrustum(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.frustum(left, right, bottom, top, near, far);
        program.setUniform(uniProj, proj);
    }
    
    public static ShaderProgram getProgram(){
        
        return program;
    }
    
    public static void setMode(Mode m){
        switch(m){
            case OBJECT:
                program.setUniform(mode, (int) 0);
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
            case SHADOW:
                program.setUniform(mode, (int) 6);
        }
    }
    
    public static void checkError(){
        
        program.checkStatus();
    }
    
    public static void setShadowLightMatrix(Matrix4f m){
        
        program.setUniform(shadow, m);
    }
    
    public static void setView(Camera c){
       
        Vector3f rot = c.getRot();       
  
        view = new Matrix4f().rotate(rot.x,1,0,0).rotate(rot.y,0,1,0).rotate(rot.z,0,0,1).translate(c.getPos());
        
        setView(view);
    }
    
    public static Matrix4f getMVP(){
        return proj.multiply(view).multiply(model);
    }
    
    public static void setUVMultX(float f){
        program.setUniform(uvx, (float)f);
    }
    
    public static void setUVMultY(float f){
        program.setUniform(uvy, (float)f);
    }
    
    public static void setInstanced(boolean instanced){
        program.setUniform(inst, instanced);
    }
    
    public static void passMaterial(Material m){
        program.setUniform(specularpow, (float) m.nsExponent);
        program.setUniform(ambient, new Vector3f((float)m.ka.x,(float)m.ka.y,(float)m.ka.z));
        program.setUniform(specularcolor, new Vector3f(1,1,1));
        //program.setUniform(specularcolor, new Vector3f((float)m.ks.x,(float)m.ks.y,(float)m.ks.z));
        program.setUniform(hasspec, m.hasspecmap);
        program.setUniform(hasnorm, m.hasnormmap);
        program.setUniform(hasspecpow, m.hasspecpow);
        program.setUniform(hasamb, m.hasreflmap);
    }
    
    public static void setBillBoard(boolean yes){  
        program.setUniform(billboard,yes);
    }
    
    public static void setLight(Light light){
        program.setUniform(lightpos, light.pos);
    }
}
