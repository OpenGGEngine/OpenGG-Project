/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.GLBuffer;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ShaderController {
    private static Matrix4f model= new Matrix4f(), view= new Matrix4f(), proj = new Matrix4f();
    private static HashMap<String, Program> programs = new HashMap<>();
    private static HashMap<String, Pipeline> pipelines = new HashMap<>(); 
    private static HashMap<String, String> rnames = new HashMap<>();
    private static List<String> searchedUniforms = new ArrayList<>();
    private static List<String> searchedAttribs = new ArrayList<>();
    private static String curv, curg, curf;
    private static int currentBind = 0;
    
    public static void initialize(){
        loadShader("mainvert", Resource.getShaderPath("object.vert"), Program.VERTEX);
        loadShader("passthroughvert", Resource.getShaderPath("passthrough.vert"), Program.VERTEX);

        loadShader("maingeom", Resource.getShaderPath("object.geom"), Program.GEOMETRY);
        loadShader("passthroughgeom", Resource.getShaderPath("passthrough.geom"), Program.GEOMETRY);
        loadShader("volumegeom", Resource.getShaderPath("volume.geom"), Program.GEOMETRY);
        loadShader("mainadjgeom", Resource.getShaderPath("objectadj.geom"), Program.GEOMETRY);
        loadShader("passthroughadjgeom", Resource.getShaderPath("passthroughadj.geom"), Program.GEOMETRY);
        
        loadShader("mainfrag", Resource.getShaderPath("phong.frag"), Program.FRAGMENT);
        loadShader("passthroughfrag", Resource.getShaderPath("passthrough.frag"), Program.FRAGMENT);
        loadShader("ssaofrag", Resource.getShaderPath("ssao.frag"), Program.FRAGMENT);  
        loadShader("cubemapfrag", Resource.getShaderPath("cubemap.frag"), Program.FRAGMENT); 
        loadShader("ambientfrag", Resource.getShaderPath("ambient.frag"), Program.FRAGMENT); 
        loadShader("texturefrag", Resource.getShaderPath("texture.frag"), Program.FRAGMENT);
        loadShader("terrainfrag", Resource.getShaderPath("terrainmulti.frag"), Program.FRAGMENT);
        //loadShader("bloomfrag", Resource.getShaderPath("bloom.frag"), Program.FRAGMENT);  
        loadShader("addfrag", Resource.getShaderPath("add.frag"), Program.FRAGMENT);  
        
        use("mainvert", "maingeom", "mainfrag");
        saveCurrentConfiguration("object");
        
        use("mainvert", "mainadjgeom", "mainfrag");
        saveCurrentConfiguration("adjobject");
        
        use("mainvert", "maingeom", "terrainfrag");
        saveCurrentConfiguration("terrain");
        
        use("mainvert", "passthroughgeom", "ambientfrag");
        saveCurrentConfiguration("ambient");
        
        use("mainvert", "passthroughadjgeom", "ambientfrag");
        saveCurrentConfiguration("adjambient");
        
        use("passthroughvert", "passthroughgeom", "ssaofrag");
        saveCurrentConfiguration("ssao");
        
        //use("passthroughvert", "passthroughgeom", "bloomfrag");
        //saveCurrentConfiguration("bloom");

        use("passthroughvert", "passthroughgeom", "passthroughfrag");
        saveCurrentConfiguration("passthrough");
        
        use("passthroughvert", "passthroughadjgeom", "passthroughfrag");
        saveCurrentConfiguration("adjpassthrough");
        
        use("passthroughvert", "passthroughgeom", "cubemapfrag");
        saveCurrentConfiguration("sky");
        
        use("passthroughvert", "volumegeom", "passthroughfrag");
        saveCurrentConfiguration("volume");
        
        use("passthroughvert", "passthroughgeom", "texturefrag");
        saveCurrentConfiguration("texture");
        
        use("passthroughvert", "passthroughgeom", "addfrag");
        saveCurrentConfiguration("add");
        
        GGConsole.log("Default shaders loaded and validated");

        /* Set shader variables */

        findUniform("inst");
        setUniform("inst", 0);

        findUniform("text");
        setUniform("text", false);

        findUniform("model");
        setUniform("model", new Matrix4f());

        findUniform("projection");
        setUniform("projection", new Matrix4f());

        findUniform("cubemap");
        setTextureLocation("cubemap", 2);

        findUniform("skycolor");
        setUniform("skycolor", new Vector3f(0.5f,0.5f,0.5f));

        findUniform("divAmount");
        setUniform("divAmount", 1f);

        findUniform("uvmultx");
        setUniform("uvmultx", (float)1f);

        findUniform("uvmulty");
        setUniform("uvmulty", (float)1f);

        findUniform("rot");
        setUniform("rot", new Vector3f(0,0,0));

        findUniform("view");
        setUniform("view", new Matrix4f());
        
        findUniform("numLights");
        setUniform("numLights", 1);

        findUniform("time");
        setUniform("time", 0f);
        
        findUniform("billboard");
        setUniform("billboard", false);
        
        setMatLinks();

        checkError();     
    }
    
    private static void setMatLinks(){
        findUniform("Kd"); 
        setTextureLocation("Kd", 0);
        
        findUniform("Ka");
        setTextureLocation("Ka", 1);
        
        findUniform("bump");
        setTextureLocation("bump", 3);
        
        findUniform("Ks"); 
        setTextureLocation("Ks", 4);
        
        findUniform("Ns"); 
        setTextureLocation("Ns", 5);
        
        findUniform("material.ka");
        setUniform("material.ka", new Vector3f());
        
        findUniform("material.kd");
        setUniform("material.kd", new Vector3f());
        
        findUniform("material.ks");
        setUniform("material.ks", new Vector3f());
        
        findUniform("material.ns");
        setUniform("material.ns", 0f);
        
        findUniform("material.hasspecmap");
        setUniform("material.hasspecmap", false);
        
        findUniform("material.hasnormmap");
        setUniform("material.hasnormmap", false);
        
        findUniform("material.hasambmap");
        setUniform("material.hasambmap", false);
        
        findUniform("material.hasspecpow");
        setUniform("material.hasspecpow", false);
        
        findUniform("material.hascolormap");
        setUniform("material.hascolormap", false);
    }  
    
    public static void setLightPos(Vector3f pos){ 
        setUniform("light.lightpos", pos);
    }
    
    public static void setModel(Matrix4f model){
        setUniform("model", model);
    }
    
    public static void setTimeMod(float mod){
        setUniform("time", mod);
    }
    
    public static void setView(Matrix4f view){
        setUniform("view", view);
    }
    
    public static void setDistanceField(boolean distfield){
        setUniform("text", distfield);
    }
    
    public static void setPerspective(float fov, float aspect, float znear, float zfar){
        proj = Matrix4f.perspective(fov, aspect, znear, zfar);
        setUniform("projection", proj);
    }
    
    public static void setOrtho(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.orthographic(left, right, bottom, top, near, far);
        setUniform("projection", proj);
    }
    
    public static void setFrustum(float left, float right, float bottom, float top, float near, float far){
        proj = Matrix4f.frustum(left, right, bottom, top, near, far);
        setUniform("projection", proj);
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
    
    public static void findAttribLocation(String loc){
        for(String s : searchedAttribs)
            if(s.equals(loc))
                return;

        programs.values().stream().filter((p) -> (p.type == Program.VERTEX)).forEach((p) -> {
            p.findAttributeLocation(loc);
        });
        
        searchedAttribs.add(loc);
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
        if(searchedUniforms.contains(loc))
            return;
        for(Program p : programs.values()){
            p.findUniformLocation(loc);
        }
        searchedUniforms.add(loc);
    }
    
    public static void setUniform(String s, Vector3f v3){
        for(Program p : programs.values()){p.setUniform(
                p.getUniformLocation(s), v3);
        }
    }
    
    public static void setUniform(String s, Vector2f v2){
        for(Program p : programs.values()){p.setUniform(
                p.getUniformLocation(s), v2);
        }
    }
    
    public static void setUniform(String s, Matrix4f m4){
        for(Program p : programs.values()){
            p.setUniform(p.getUniformLocation(s), m4);
        }
    }
    
    public static void setUniform(String s, int i){
        for(Program p : programs.values()){
            p.setUniform(p.getUniformLocation(s), i);
        }
    }
    
    public static void setUniform(String s, float f){
        for(Program p : programs.values()){
            p.setUniform(p.getUniformLocation(s), f);
        }
    }
    
    public static void setUniform(String s, boolean b){
        for(Program p : programs.values()){
            p.setUniform(p.getUniformLocation(s), b);
        }
    }
       
    public static void setTextureLocation(String s, int i){
        programs.values().stream().filter((p) -> (p.type == Program.FRAGMENT)).forEach((p) -> {
            p.setUniform(p.getUniformLocation(s), i);
        });
    }
    
    public static int getUniqueUniformBufferLocation(){
        int n = currentBind;
        currentBind++;
        return n;
    }
    
    public static void setUniformBlockLocation(GLBuffer ubo, String name){
        setUniformBlockLocation(ubo.getBase(), name);
    }
    
    public static void setUniformBlockLocation(int bind, String name){
        for(Program p : programs.values()){
            p.setUniformBlockIndex(bind, name);
        }
    }
    
    public static void checkError(){
        for(Program p : programs.values()){
            p.checkStatus();
        }
        for(Pipeline p : pipelines.values()){
            p.validate();
        }
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
        setUniform("material.ns", (float) m.nsExponent);
        setUniform("material.ka", m.ka);
        setUniform("material.kd", m.kd);
        setUniform("material.ks", m.ks);
        setUniform("material.hasspecmap", m.hasspecmap);
        setUniform("material.hasnormmap", m.hasnormmap);
        setUniform("material.hasspecpow", m.hasspecpow);
        setUniform("material.hasambmap", m.hasreflmap);
        setUniform("material.hascolormap", m.hascolmap);
    }
    
    public static void setBillBoard(boolean yes){  
        setUniform("billboard", yes);
    }
    
    private static void use(Program v, Program g, Program f){
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
        curv = v;
        curg = g;
        curf = f;
        use(programs.get(v), programs.get(g), programs.get(f));
    }
    
    public static void saveConfiguration(String v, String g, String f, String name){
        Program vp = programs.get(v);
        Program gp = programs.get(g);
        Program fp = programs.get(f);
        
        String st = Integer.toString(vp.id) + Integer.toString(gp.id) + Integer.toString(fp.id);
        
        rnames.put(name, st);
    }
    
    public static void saveCurrentConfiguration(String name){
        saveConfiguration(curv, curg, curf, name);
    }
    
    public static void useConfiguration(String name){
        String id = rnames.get(name);
        Pipeline p = pipelines.get(id);
               
        curv = p.vert.name;
        curg = p.geom.name;
        curf = p.frag.name;
        
        p.bind();
    }
    
    public static void clearPipelineCache(){
        for(Pipeline p : pipelines.values()){
            p.deletePipeline();
        }
        pipelines.clear();
        pipelines = new HashMap<>();
    }
    
    public static boolean loadShader(String name, String loc, int type){
        try {
            CharSequence sec = FileStringLoader.loadStringSequence(URLDecoder.decode(loc, "UTF-8"));
            programs.put(name, new Program(type, sec, name));
            Program p = programs.get(name);
            for(String s : searchedUniforms){
                p.findUniformLocation(s);
            }
            p.checkStatus();
            return true;
        } catch (UnsupportedEncodingException ex) {
            GGConsole.error("Failed to load shader: " + name);
            return false;
        }
    }
}
