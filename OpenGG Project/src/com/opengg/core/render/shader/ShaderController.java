/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.Light;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.shader.opengl3.ShaderProgram;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.world.Camera;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;

/**
 *
 * @author Javier
 */
public class ShaderController {
    private static HashMap<String, Program> programs = new HashMap<>();
    private static HashMap<String, Pipeline> pipelines = new HashMap<>(); 
    private static String curv, curg, curf;
    private static List<String> searched;
    private static float ratio;
    private static Matrix4f model= new Matrix4f(), view= new Matrix4f(), proj = new Matrix4f();
    
    public static void initialize(URL vert, URL frag, URL geom){
        try {
            CharSequence v1 = FileStringLoader.loadStringSequence(
                            URLDecoder.decode(
                                    vert.getFile(), "UTF-8"));
            CharSequence g1 = FileStringLoader.loadStringSequence(
                            URLDecoder.decode(
                                    geom.getFile(), "UTF-8"));
            CharSequence f1 = FileStringLoader.loadStringSequence(
                            URLDecoder.decode(
                                    frag.getFile(), "UTF-8"));
            
            programs.put("mainvert", new Program(Program.VERTEX, v1));
            programs.put("maingeom", new Program(Program.GEOMETRY, g1));
            programs.put("mainfrag", new Program(Program.FRAGMENT, f1));
            
            use("mainvert", "maingeom", "mainfrag");
            checkError();
            System.exit(0);
            
            GGConsole.log("Shader files loaded and validated");
        } catch (UnsupportedEncodingException ex) {
            GGConsole.error("Unable to parse shader files!");
            return;
        }

        initVertexAttributes();
        
        GGConsole.log("Shaders linked, attributes have been validated");

        /* Set shader variables */

        findUniform("inst");
        setUniform("inst", 0);

        findUniform("text");
        setUniform("text", false);

        findUniform("model");
        setUniform("uniModel", new Matrix4f());

        findUniform("projection");
        setUniform("uniProj", new Matrix4f());

        findUniform("cubemap");
        setUniform("uniCube", 2);

        findUniform("skycolor");
        setUniform("uniskycolor", new Vector3f(0.5f,0.5f,0.5f));

        findUniform("light.lightpos");
        setUniform("lightpos", new Vector3f(200,50,-10));

        findUniform("divAmount");
        setUniform("div", 1f);

        findUniform("uvmultx");
        setUniform("uvx", (float)1f);

        findUniform("uvmulty");
        setUniform("uvy", (float)1f);

        findUniform("rot");
        setUniform("rotm", new Vector3f(0,0,0));

        findUniform("view");
        setUniform("uniView", new Matrix4f());

        findUniform("light.lightdistance");
        setUniform("lightdistance", 250f);

        findUniform("light.lightpower");
        setUniform("lightpower", 100f);

        findUniform("light.color");
        setUniform("lightcolor", new Vector3f(1,1,1));

        findUniform("mode");
        setUniform("mode", (int) 0);

        findUniform("time");
        setUniform("time", 0f);

        setMatLinks();

        findUniform("billboard");
        setUniform("billboard", false);
        checkError();     
    }
    
    private static void setMatLinks(){
        findUniform("Kd"); 
        setUniform("uniTex", 0);
        
        findUniform("Ka");
        setUniform("uniAmb", 1);
        
        findUniform("bump");
        setUniform("uniNorm", 3);
        
        findUniform("Ks"); 
        setUniform("uniSpec", 4);
        
        findUniform("Ns"); 
        setUniform("uniExp", 5);
        
        findUniform("material.ka");
        setUniform("ambient", 1f);
        
        findUniform("material.ks");
        setUniform("specularcolor", new Vector3f());
        
        findUniform("material.ns");
        setUniform("specularpow", 0f);
        
        findUniform("material.hasspecmap");
        setUniform("hasspec", false);
        
        findUniform("material.hasnormmap");
        setUniform("hasnorm", false);
        
        findUniform("material.hasambmap");
        setUniform("hasamb", false);
        
        findUniform("material.hasspecpow");
        setUniform("hasspecpow", false);
    }
    
    public static void initVertexAttributes() {
        findAttribLocation("position");
        findAttribLocation("color");      
        findAttribLocation("normal");       
        findAttribLocation("texcoord"); 

    }
    
    public static void defVertexAttributes(){
        enableVertexAttribute("position");
        enableVertexAttribute("color");      
        enableVertexAttribute("normal"); 
        enableVertexAttribute("texcoord");   
        setVertexAttribDivisor("color", 0);
    }
    
    public static void pointVertexAttributes(){
        pointVertexAttribute("position", 3, 12 * Float.BYTES, 0);
        pointVertexAttribute("color", 4, 12 * Float.BYTES, 3 * Float.BYTES);
        pointVertexAttribute("normal", 3, 12 * Float.BYTES, 7 * Float.BYTES);
        pointVertexAttribute("texcoord", 2, 12 * Float.BYTES, 10 * Float.BYTES);     
    }
    
    public static void defInstancedVertexAttributes(VertexBufferObject b){
        enableVertexAttribute("position");
        pointVertexAttribute("position", 3, 12 * Float.BYTES, 0);

        enableVertexAttribute("normal");
        pointVertexAttribute("normal", 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        enableVertexAttribute("texcoord");
        pointVertexAttribute("texcoord", 2, 12 * Float.BYTES, 10 * Float.BYTES);
        
        b.bind(GL_ARRAY_BUFFER);
        
        enableVertexAttribute("color");
        pointVertexAttribute("color", 4, 3 * Float.BYTES, 0);
        setVertexAttribDivisor("color", 1);

    }
    
    public static void setLightPos(Vector3f pos){ 
        setUniform("lightpos", pos);
    }
    
    public static void setModel(Matrix4f model){
        setUniform("uniModel", model);
    }
    
    public static void setTimeMod(float mod){
        setUniform("time", mod);
    }
    
    public static void setView(Matrix4f view){
        
        setUniform("uniView", view);
    }
    
    public static void setDistanceField(boolean distfield){
        setUniform("text", distfield);
    }
    
    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        proj = Matrix4f.perspective(fov, aspect, znear, zfar);
        setUniform("uniProj", proj);
    }
    
    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.orthographic(left, right, bottom, top, near, far);
        setUniform("uniProj", proj);
    }
    
    public static void setFrustum(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.frustum(left, right, bottom, top, near, far);
        setUniform("uniProj", proj);
    }
    
    public static void findAttribLocation(String loc){
        programs.values().stream().filter((p) -> (p.type == Program.VERTEX)).forEach((p) -> {
            p.findAttributeLocation(loc);
        });
    }
    
    public static void enableVertexAttribute(String loc){
        programs.get(curv).enableVertexAttribute(loc);
    }
    
    public static void disableVertexAttribute(String loc){
        programs.get(curv).enableVertexAttribute(loc);
    }
    
    public static void pointVertexAttribute(String loc, int size, int tot, int start){
        programs.get(curv).pointVertexAttribute(loc, size, tot, start);
    }
    
    public static void setVertexAttribDivisor(String loc, int idk){
        programs.get(curv).setVertexAttribDivisor(loc, idk);
    }
    
    public static void findUniform(String loc){
        for(Program p : programs.values()){
            p.findUniformLocation(loc);
        }
        searched.add(loc);
    }
    
    public static void setUniform(String s, Vector3f v3){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), v3);}
    }
    
    public static void setUniform(String s, Vector2f v2){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), v2);}
    }
    
    public static void setUniform(String s, Matrix4f m4){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), m4);}
    }
    
    public static void setUniform(String s, int i){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), i);}
    }
    
    public static void setUniform(String s, float f){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), f);}
    }
    
    public static void setUniform(String s, boolean b){
        for(Program p : programs.values()){p.setUniform(p.getUniformLocation(s), b);}
    }
    
    public static void setMode(Mode m){
        switch(m){
            case OBJECT:
                setUniform("mode", (int) 0);
                break;
            case GUI:
                setUniform("mode", (int) 2);
                break;
            case SKYBOX:
                setUniform("mode", (int) 3);
                break;
            case POS_ONLY:
                setUniform("mode", (int) 4);
                break;
            case PP:
                setUniform("mode", (int) 5);
                break;
            case SHADOW:
                setUniform("mode", (int) 6);
        }
    }
    
    public static void checkError(){
        for(Program p : programs.values()){
            //p.checkStatus();
        }
        for(Pipeline p : pipelines.values()){
            p.validate();
        }
    }
    
    public static void setShadowLightMatrix(Matrix4f m){
        setUniform("shadow", m);
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
        setUniform("uvx", (float)f);
    }
    
    public static void setUVMultY(float f){
        setUniform("uvy", (float)f);
    }
    
    public static void setInstanced(boolean instanced){
        setUniform("inst", instanced);
    }
    
    public static void passMaterial(Material m){
        setUniform("specularpow", (float) m.nsExponent);
        setUniform("ambient", new Vector3f((float)m.ka.x,(float)m.ka.y,(float)m.ka.z));
        setUniform("specularcolor", new Vector3f(1,1,1));
        //setUniform("specularcolor, new Vector3f((float)m.ks.x,(float)m.ks.y,(float)m.ks.z));
        setUniform("hasspec", m.hasspecmap);
        setUniform("hasnorm", m.hasnormmap);
        setUniform("hasspecpow", m.hasspecpow);
        setUniform("hasamb", m.hasreflmap);
    }
    
    public static void setBillBoard(boolean yes){  
        setUniform("billboard", yes);
    }
    
    public static void setLight(Light light){
        setUniform("lightpos", light.pos);
    }
    
    public static void use(Program v, Program g, Program f){
        String st = Integer.toString(v.id) + Integer.toString(g.id) + Integer.toString(f.id);
        Pipeline p;
        if((p = pipelines.get(st)) != null){
            p.bind();
            return;
        }
        p = new Pipeline(v,g,f);
        pipelines.put(st, p);
        p.bind();
    }
    
    public static void use(String v, String g, String f){
        use(programs.get(v), programs.get(g), programs.get(f));
    }
}
