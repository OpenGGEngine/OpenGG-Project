/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.GraphicsBuffer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.opengg.core.render.shader.ShaderProgram.ShaderType;

/**
 * Controller/manager for GLSL shaders
 * @author Javier
 */
public class ShaderController {
    private static String currentshader = "";
    private static final Matrix4f model = new Matrix4f();
    private static Matrix4f view = new Matrix4f(), proj = new Matrix4f();
    private static final Map<String, ShaderProgram> programs = new HashMap<>();
    private static final Map<String, ShaderPipeline> pipelines = new HashMap<>();
    private static final Map<String, String> rnames = new HashMap<>();
    private static final List<String> searchedUniforms = new ArrayList<>();
    private static final List<String> searchedAttribs = new ArrayList<>();
    private static String currentvert, currenttesc, currenttese, currentgeom, currentfrag;
    private static int currentBind = 0;

    /**
     * Initializes the controller and loads all default shaders
     */
    public static void initialize() {
        loadShader("mainvert", Resource.getShaderPath("object.vert"), ShaderType.VERTEX);
        loadShader("animvert", Resource.getShaderPath("anim.vert"), ShaderType.VERTEX);
        loadShader("particlevert", Resource.getShaderPath("particle.vert"), ShaderType.VERTEX);
        loadShader("passthroughvert", Resource.getShaderPath("passthrough.vert"), ShaderType.VERTEX);

        loadShader("maingeom", Resource.getShaderPath("object.geom"), ShaderType.GEOMETRY);
        loadShader("passthroughgeom", Resource.getShaderPath("passthrough.geom"), ShaderType.GEOMETRY);
        loadShader("volumegeom", Resource.getShaderPath("volume.geom"), ShaderType.GEOMETRY);
        loadShader("mainadjgeom", Resource.getShaderPath("objectadj.geom"), ShaderType.GEOMETRY);
        loadShader("passthroughadjgeom", Resource.getShaderPath("passthroughadj.geom"), ShaderType.GEOMETRY);

        loadShader("mainfrag", Resource.getShaderPath("phong.frag"), ShaderType.FRAGMENT);
        loadShader("shadowfrag", Resource.getShaderPath("phongshadow.frag"), ShaderType.FRAGMENT);
        loadShader("passthroughfrag", Resource.getShaderPath("passthrough.frag"), ShaderType.FRAGMENT);
        loadShader("ssaofrag", Resource.getShaderPath("ssao.frag"), ShaderType.FRAGMENT);
        loadShader("cubemapfrag", Resource.getShaderPath("cubemap.frag"), ShaderType.FRAGMENT);
        loadShader("ambientfrag", Resource.getShaderPath("ambient.frag"), ShaderType.FRAGMENT);
        loadShader("texturefrag", Resource.getShaderPath("texture.frag"), ShaderType.FRAGMENT);
        loadShader("terrainfrag", Resource.getShaderPath("terrainmulti.frag"), ShaderType.FRAGMENT);
        //loadShader("bloomfrag", Resource.getShaderPath("bloom.frag"), OpenGLShaderProgram.FRAGMENT);
        loadShader("addfrag", Resource.getShaderPath("add.frag"), ShaderType.FRAGMENT);
        loadShader("guifrag", Resource.getShaderPath("gui.frag"), ShaderType.FRAGMENT);
        loadShader("barfrag", Resource.getShaderPath("bar.frag"), ShaderType.FRAGMENT);
        loadShader("hdrfrag", Resource.getShaderPath("hdr.frag"), ShaderType.FRAGMENT);
        loadShader("waterfrag", Resource.getShaderPath("water.frag"), ShaderType.FRAGMENT);
          
        use("mainvert", "mainfrag");
        saveCurrentConfiguration("object");   
        
        use("animvert", "mainfrag");
        saveCurrentConfiguration("animation");  
        
        use("mainvert", "shadowfrag");
        saveCurrentConfiguration("shadobject");   
        
        use("mainvert", "terrainfrag");
        saveCurrentConfiguration("terrain");
        
        use("mainvert", "ambientfrag");
        saveCurrentConfiguration("ambient");     
        
        use("mainvert", "waterfrag");
        saveCurrentConfiguration("water"); 
        
        use("passthroughvert", "ssaofrag");
        saveCurrentConfiguration("ssao");
        
       // use("passthroughvert", "bloomfrag");
       // saveCurrentConfiguration("bloom");
        
        use("passthroughvert", "hdrfrag");
        saveCurrentConfiguration("hdr");

        use("passthroughvert", "passthroughfrag");
        saveCurrentConfiguration("passthrough");
             
        use("passthroughvert", "cubemapfrag");
        saveCurrentConfiguration("sky");
        
        use("passthroughvert", "passthroughfrag");
        saveCurrentConfiguration("volume");
        
        use("passthroughvert", "texturefrag");
        saveCurrentConfiguration("vulkan");
        
        use("passthroughvert", "guifrag");
        saveCurrentConfiguration("gui");
        
        use("passthroughvert", "barfrag");
        saveCurrentConfiguration("bar");
        
        use("passthroughvert", "addfrag");
        saveCurrentConfiguration("add");
        
        use("particlevert", "texturefrag");
        saveCurrentConfiguration("particle");

        /* Set shader variables */

        findUniform("inst");
        setUniform("inst", 0);

        findUniform("text");
        setUniform("text", 0);

        findUniform("model");
        setUniform("model", new Matrix4f());
        
         findUniform("jointsMatrix");
        setUniform("jointsMatrix", new Matrix4f[50]);

        findUniform("projection");
        setUniform("projection", new Matrix4f());
        
        findUniform("cubemap");
        setTextureLocation("cubemap", 2);

        findUniform("skycolor");
        setUniform("skycolor", new Vector3f(0.5f,0.5f,0.5f));

        findUniform("divAmount");
        setUniform("divAmount", 1f);
        
        findUniform("percent");
        setUniform("percent", 1f);

        findUniform("uvmultx");
        setUniform("uvmultx", 1f);

        findUniform("uvmulty");
        setUniform("uvmulty", 1f);
        
        findUniform("uvoffsetx");
        setUniform("uvoffsetx", 0f);

        findUniform("uvoffsety");
        setUniform("uvoffsety", 0f);

        findUniform("rot");
        setUniform("rot", new Vector3f(0,0,0));
        
        findUniform("camera");
        setUniform("camera", new Vector3f(0,0,0));

        findUniform("view");
        setUniform("view", new Matrix4f());
        
        findUniform("numLights");
        setUniform("numLights", 1);

        findUniform("time");
        setUniform("time", 0f);
        
        findUniform("billboard");
        setUniform("billboard", false);
        
        findUniform("exposure");
        setUniform("exposure", 1f);
        
        findUniform("gamma");
        setUniform("gamma", 2.2f);
        
        findUniform("shadowmap"); 
        setTextureLocation("shadowmap", 6);

        findUniform("shadowmap2"); 
        setTextureLocation("shadowmap2", 7);
        
        findUniform("shadowmap3"); 
        setTextureLocation("shadowmap3", 8);
        
        
        setMatLinks();

        checkError();   
        
        GGConsole.log("Shader Controller initialized, loaded " + programs.size() + " shader programs");
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
        setUniform("material.ns", 32.0f);
        
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
    
    public static void setDistanceField(int distfield){
        setUniform("text", distfield);
    }
    
    public static void setView(Matrix4f view){
        ShaderController.view = view;
        setUniform("view", view);
    }
    
    public static void setProjection(Matrix4f proj){
        setUniform("projection", proj);
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
    
    public static Matrix4f getMVP(){
        return proj.multiply(view).multiply(model);
    }
    
    public static void setUVMultX(float f){
        setUniform("uvmultx", f);
    }
    
    public static void setUVMultY(float f){
        setUniform("uvmulty", f);
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
    
    public static void setBillBoard(int yes){  
        setUniform("billboard", yes);
    }
    
    /**
     * Gets the location of the given attribute in all vertex shaders and saves it per shader
     * @param loc Attribute to search for, must be the same name as the one in GLSL
     */
    public static void findAttribLocation(String loc){
        for(String s : searchedAttribs)
            if(s.equals(loc))
                return;

        programs.values().stream().filter((p) -> (p.getType() == ShaderProgram.ShaderType.VERTEX)).forEach((p) -> {
            p.findAttributeLocation(loc);
        });
        
        searchedAttribs.add(loc);
    }
    
    /**
     * Enables the given vertex attribute in the current vertex shader
     * @param loc Attribute to enable
     */
    public static void enableVertexAttribute(String loc){
        programs.get(currentvert).enableVertexAttribute(loc);
    }
    
    /**
     * Disables the given vertex attribute in the current vertex shader
     * @param loc Attribute to disable
     */
    public static void disableVertexAttribute(String loc){
        programs.get(currentvert).enableVertexAttribute(loc);
    }
    
    /**
     * Sets how the attribute should get values from the currently bound OpenGL buffer for rendering
     * @param loc Name of attribute
     * @param size Size of attribute value in bytes (ex. 4 for {@code int} )
     * @param type Type of value in the buffer, either GL_INT, GL_FLOAT, or GL_BYTE
     * @param tot Total size of vertex in the given buffer (ex. A buffer containing 3 {@code ints} per vertex would have size 12)
     * @param start Starting point of attribute per vertex 
     * (ex. The third attribute in a buffer containing 3 {@code ints} would have starting point 8)
     */
    public static void pointVertexAttribute(String loc, int size, int type, int tot, int start){
        programs.get(currentvert).pointVertexAttribute(loc, size, type, tot, start);
    }
    
    
    /**
     * Sets the amount of times the object should be rendered glDrawElementsInstanced before the attribute goes to the next value, 0 for per vertex
     * @param loc Attribute to be instanced
     * @param idk Renders per value change, 0 for normal rendering
     */
    public static void setVertexAttribDivisor(String loc, int idk){
        programs.get(currentvert).setVertexAttribDivisor(loc, idk);
    }
    
    /**
     * Finds and saves the location of the uniform with name {@code loc} in every attached shader
     * @param loc Name of uniform
     */
    public static void findUniform(String loc){
        if(searchedUniforms.contains(loc))
            return;
        for(ShaderProgram p : programs.values()){
            p.findUniformLocation(loc);
        }
        searchedUniforms.add(loc);
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, Vector3f val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, Vector2f val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, Matrix4f val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, Matrix4f[] val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, int val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, float val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
    
    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, boolean val){
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }
       
    /**
     * Binds the sampler named {@code name} to vulkan location {@code loc} in every fragment shader
     * @param name Name of sampler in shader
     * @param loc New location of sampler
     */
    public static void setTextureLocation(String name, int loc){
        programs.values().stream().filter((p) -> (p.getType() == ShaderProgram.ShaderType.FRAGMENT)).forEach((p) -> {
            p.setUniform(p.getUniformLocation(name), loc);
        });
    }
    
    /**
     * Returns a unique positive location for a uniform buffer object
     * @return New location
     */
    public static int getUniqueUniformBufferLocation(){
        int n = currentBind;
        currentBind++;
        return n;
    }
    
    /**
     * Binds the given uniform buffer object to the given GLSL identifier
     * @param ubo UBO to be bound, as a {@link OpenGLBuffer}
     * @param name Name of buffer in GLSL to bind UBO to
     */
    public static void setUniformBlockLocation(GraphicsBuffer ubo, String name){
        setUniformBlockLocation(ubo.getBase(), name);
    }
    
    /**
     * Binds the given uniform buffer object ID to the given GLSL identifier
     * @param id UBO ID to be bound
     * @param name Name of buffer in GLSL to bind UBO to
     */
    public static void setUniformBlockLocation(int id, String name){
        for(ShaderProgram p : programs.values()){
            p.setUniformBlockIndex(id, name);
        }
    }
    
    /**
     * Checks for errors in all shaders and shader pipelines
     */
    public static void checkError(){
        for(ShaderProgram p : programs.values()){
            p.checkStatus();
        }
        for(ShaderPipeline p : pipelines.values()){
            p.validate();
        }
    }

    
    private static String getUniqueConfigID(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        String st = "";
        st += vert.getId() + ";";

        if(tesc != null)
            st += tesc.getId();
        st += ";";

        if(tese != null)
            st += tese.getId();
        st += ";";

        if(geom != null)
            st += geom.getId();
        st += ";";
        
        st += frag.getId();
        return st;
    }
    
    private static void use(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        String id = getUniqueConfigID(vert, tesc, tese, geom, frag);
        
        ShaderPipeline pipeline;
        if((pipeline = pipelines.get(id)) != null){
            pipeline.bind();

            return;
        }else{
            pipeline = ShaderPipeline.create(vert, tesc, tese, geom, frag);
        
            pipelines.put(id, pipeline);
            pipeline.bind();
        }

        currentvert = pipeline.getShader(ShaderType.VERTEX);
        currenttesc = pipeline.getShader(ShaderType.TESS_CONTROL);
        currenttese = pipeline.getShader(ShaderType.TESS_EVAL);
        currentgeom = pipeline.getShader(ShaderType.GEOMETRY);
        currentfrag = pipeline.getShader(ShaderType.FRAGMENT);
    }
    
    /**
     * Activates the given vertex, geometry, and fragment shaders for rendering<br><br>
     * 
     * This method searches for a shader pipeline that already uses this specific combination of shaders, and uses it if available.
     * Otherwise, it creates a new pipeline, saves it, and uses it
     * @param vert Vertex shader to use
     * @param geom Geometry shader to use
     * @param frag Fragment shader to use
     */
    public static void use(String vert, String geom, String frag){
        use(programs.get(vert), null, null, programs.get(geom), programs.get(frag));
    }
    
    /**
     * Activates the given vertex and fragment shadersfor rendering<br><br>
     * 
     * This method searches for a shader pipeline that already uses this specific combination of shaders, and uses it if available.
     * Otherwise, it creates a new pipeline, saves it, and uses it
     * @param vert Vertex shader to use
     * @param frag Fragment shader to use
     */
    public static void use(String vert, String frag){
        use(programs.get(vert), null, null, null, programs.get(frag));
    }
    
    private static void saveConfiguration(String vert, String tesc, String tese, String geom, String frag, String name){
        ShaderProgram vertprogram = programs.get(vert);
        ShaderProgram tescprogram = programs.get(geom);
        ShaderProgram teseprogram = programs.get(geom);
        ShaderProgram geomprogram = programs.get(geom);
        ShaderProgram fragprogram = programs.get(frag);
        
        String id = getUniqueConfigID(vertprogram, tescprogram, teseprogram, geomprogram, fragprogram);
        
        rnames.put(name, id);
    }
    
    /**
     * Saves the current shader configuration with a given readable name for easier reuse
     * @param name Name of configuration
     */
    public static void saveCurrentConfiguration(String name){
        saveConfiguration(currentvert, currenttesc, currenttese, currentgeom, currentfrag, name);
    }
    
    /**
     * Uses the configuration with the given name<br><br>
     * 
     * This name should be the same as the one previously saved in {@link #saveCurrentConfiguration(String)}
     * 
     * @param name Name of configuration
     */
    public static void useConfiguration(String name){
        String id = rnames.get(name);
        ShaderPipeline pipeline = pipelines.get(id);
               
        if(pipeline == null){
            GGConsole.error("A shader configuration named " + name + " tried to be used, but no appropriate pipeline was found!");
            throw new ShaderException("Failed to find pipeline named " + name);
        }
        
        currentshader = name;
        
        currentvert = pipeline.getShader(ShaderType.VERTEX);
        currenttesc = pipeline.getShader(ShaderType.TESS_CONTROL);
        currenttese = pipeline.getShader(ShaderType.TESS_EVAL);
        currentgeom = pipeline.getShader(ShaderType.GEOMETRY);
        currentfrag = pipeline.getShader(ShaderType.FRAGMENT);
        
        pipeline.bind();
    }
    
    public static void clearPipelineCache(){
        for(ShaderPipeline p : pipelines.values()){
            p.delete();
        }
        pipelines.clear();
    }
    
    public static ShaderProgram getProgram(String program){
        return programs.get(program);
    }
    
    public static String getCurrentConfiguration(){
        return currentshader;
    }
    
    public static boolean loadShader(String name, String loc, ShaderType type){
        try {
            CharSequence sec = FileStringLoader.loadStringSequence(URLDecoder.decode(loc, "UTF-8"));
            programs.put(name, ShaderProgram.create(type, sec, name));
            ShaderProgram p = programs.get(name);
            for(String s : searchedUniforms){
                p.findUniformLocation(s);
            }
            p.checkStatus();
            return true;
        } catch (UnsupportedEncodingException ex) {
            GGConsole.error("Failed to load shader: " + name);
            return false;
        } catch (IOException ex) {
            GGConsole.error("Failed to find shader file for " + loc);
            return false;
        }
    }

    private ShaderController() {
    }
}
