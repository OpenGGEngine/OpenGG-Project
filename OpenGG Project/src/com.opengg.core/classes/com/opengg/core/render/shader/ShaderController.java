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
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Material;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.shader.ggsl.Parser;
import com.opengg.core.render.shader.ggsl.ShaderFile;
import com.opengg.core.system.Allocator;
import com.opengg.core.system.GGBufferUtils;
import com.opengg.core.system.SystemInfo;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.util.HashUtil;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.opengg.core.render.shader.ShaderProgram.ShaderType;
import static java.util.function.Predicate.not;

/**
 * Controller/manager for GLSL shaders
 * @author Javier
 */
public class ShaderController {
    private static int attributeCounter = 0;
    private static final Matrix4f model = new Matrix4f();
    private static final Map<String, ShaderProgram> programs = new HashMap<>();
    private static final Map<String, ShaderPipeline> pipelines = new HashMap<>();
    private static final Map<String, String> realConfigurationNames = new HashMap<>();
    private static final Map<String, ShaderFile> shaderfiles = new HashMap<>();
    private static final Map<String, Integer> attributeLocations = new HashMap<>();
    private static final List<ShaderFileHolder> completedfiles = new ArrayList<>();
    private static final List<String> searchedUniforms = new ArrayList<>();
    private static final Map<String, Object> currentUniforms = new HashMap<>();
    private static String currentShader = "";
    private static Matrix4f view = new Matrix4f(), proj = new Matrix4f();
    private static String currentvert, currenttesc, currenttese, currentgeom, currentfrag;
    private static int currentBind = 0;
    private static boolean loadFromCache = false;

    public static void testInitialize(){
        GGConsole.initialize();
        var t = System.nanoTime();
        //var ww = new ShaderFile("object", Resource.getShaderPath("light.ggsl"));

        //loadShaderFiles();
        //linkShaders();

        var time = (System.nanoTime()-t)/1_000_000f;
        //
    }

    /**
     * Initializes the controller and loads all default shaders
     */
    public static void initialize() {
        GGConsole.log("Shader Controller initializing...");

        /* Set shader variables */

        loadShaders();

        setDefaultPipelines();

        setUniforms();

        checkError();
        
        GGConsole.log("Shader Controller initialized, loaded " + programs.size() + " shader programs");
    }

    private static void loadShaders(){
        long time = System.currentTimeMillis();

        GGConsole.log("Loading shaders...");
        loadCachedShaders();
        loadShaderFiles();
        linkShaders();
        createGLShaderFromFile();
        long finaltime = System.currentTimeMillis() - time;

        GGConsole.log("Loaded shaders in " + finaltime + " milliseconds");
    }

    private static void setDefaultPipelines(){

        use("object.vert", "object.frag");
        saveCurrentConfiguration("object");

        use("object.vert", "material.frag");
        saveCurrentConfiguration("material");

        use("anim2.vert", "object.frag");
        saveCurrentConfiguration("animation2");

        use("tangent.vert", "tangent.frag");
        saveCurrentConfiguration("tangent");

        use("object.vert", "terrainmulti.frag");
        saveCurrentConfiguration("terrain");

        use("object.vert", "ambient.frag");
        saveCurrentConfiguration("ambient");

        use("object.vert", "water.frag");
        saveCurrentConfiguration("water");

        use("object.vert", "ssao.frag");
        saveCurrentConfiguration("ssao");

        use("object.vert", "hdr.frag");
        saveCurrentConfiguration("hdr");

        use("object.vert", "bright.frag");
        saveCurrentConfiguration("bright");

        use("object.vert", "passthrough.frag");
        saveCurrentConfiguration("passthrough");

        use("modeltransform.vert", "point.geom", "point.frag");
        saveCurrentConfiguration("pointshadow");

        use("object.vert", "cubemap.frag");
        saveCurrentConfiguration("sky");

        use("object.vert", "fxaa.frag");
        saveCurrentConfiguration("fxaa");

        use("object.vert", "passthrough.frag");
        saveCurrentConfiguration("volume");

        use("object.vert", "text.frag");
        saveCurrentConfiguration("sdf");

        use("object.vert", "color_alpha.frag");
        saveCurrentConfiguration("ttf");

        use("object.vert", "texture.frag");
        saveCurrentConfiguration("texture");

        use("object.vert", "cuboid_scaling.frag");
        saveCurrentConfiguration("cuboid");

        use("object.vert", "gui.frag");
        saveCurrentConfiguration("gui");

        use("object.vert", "bar.frag");
        saveCurrentConfiguration("bar");

        use("object.vert", "add.frag");
        saveCurrentConfiguration("add");

        use("object.vert", "gaussh.frag");
        saveCurrentConfiguration("blurh");

        use("object.vert", "gaussv.frag");
        saveCurrentConfiguration("blurv");

        use("particle.vert", "texture.frag");
        saveCurrentConfiguration("particle");

        GGConsole.log("Created " + pipelines.size() + " default shader pipelines");
    }

    private static void setUniforms(){
        findUniform("inst");
        setUniform("inst", 0);

        findUniform("impl");
        setUniform("impl", 0);

        findUniform("model");
        setUniform("model", new Matrix4f());

        findUniform("view");
        setUniform("view", new Matrix4f());

        findUniform("jointsMatrix");
        setUniform("jointsMatrix", new Matrix4f[200]);

        findUniform("projection");
        setUniform("projection", new Matrix4f());

        findUniform("cubemap");
        setTextureLocation("cubemap", 2);

        findUniform("divAmount");
        setUniform("divAmount", 1f);

        findUniform("percent");
        setUniform("percent", 1f);

        findUniform("shadow");
        setUniform("shadow", 1);

        findUniform("uvmultx");
        setUniform("uvmultx", 1f);

        findUniform("uvmulty");
        setUniform("uvmulty", 1f);

        findUniform("uvoffsetx");
        setUniform("uvoffsetx", 0f);

        findUniform("uvoffsety");
        setUniform("uvoffsety", 0f);

        findUniform("cuboidscale");
        setUniform("cuboidscale", new Vector3f(1,1,1));

        findUniform("cuboidscaling");
        setUniform("cuboidscaling", 0f);

        findUniform("fill");
        setUniform("fill", new Vector3f());

        findUniform("back");
        setUniform("back", new Vector3f());

        findUniform("rot");
        setUniform("rot", new Vector3f(0,0,0));

        findUniform("scale");
        setUniform("scale", new Vector3f(1,1,1));

        findUniform("camera");
        setUniform("camera", new Vector3f(0,0,0));

        findUniform("color");
        setUniform("color", new Vector3f(1,1,1));

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

        findUniform("shadowMatrices");

        findUniform("shadowmap");
        setTextureLocation("shadowmap", 6);

        findUniform("shadowmap2");
        setTextureLocation("shadowmap2", 7);

        findUniform("shadowmap3");
        setTextureLocation("shadowmap3", 8);

        findUniform("cube");
        setTextureLocation("cube", 9);

        findUniform("shadowcube");
        setTextureLocation("shadowcube", 10);

        findUniform("cube3");
        setTextureLocation("cube3", 11);

        findUniform("lightPos");
        findUniform("farplane");

        setMatLinks();
    }

    private static void setMatLinks(){
        findUniform("Kd"); 
        setTextureLocation("Kd", 0);

        findUniform("terrain");
        setTextureLocation("terrain", 11);
        
        findUniform("Ka");
        setTextureLocation("Ka", 1);
        
        findUniform("bump");
        setTextureLocation("bump", 3);
        
        findUniform("Ks"); 
        setTextureLocation("Ks", 4);
        
        findUniform("Ns");
        setTextureLocation("Ns", 5);

        findUniform("em");
        setTextureLocation("em", 9);
        
        findUniform("material.ka");
        setUniform("material.ka", new Vector3f());
        
        findUniform("material.kd");
        setUniform("material.kd", new Vector3f());
        
        findUniform("material.ks");
        setUniform("material.ks", new Vector3f());
        
        findUniform("material.ns");
        setUniform("material.ns", 32.0f);
        
        findUniform("material.hasspec");
        setUniform("material.hasspec", false);

        findUniform("material.hasspecpow");
        setUniform("material.hasspecpow", false);
        
        findUniform("material.hasnormmap");
        setUniform("material.hasnormmap", false);
        
        findUniform("material.hasambmap");
        setUniform("material.hasambmap", false);
        
        findUniform("material.hascolormap");
        setUniform("material.hascolormap", false);

        findUniform("material.hasem");
        setUniform("material.hasem", false);
    }  
    
    public static void setLightPos(Vector3f pos){ 
        setUniform("light.lightpos", pos);
    }

    /**
     * Sets the model uniform by calculating it from the given position, rotation, and scale values
     * @param position
     * @param rotation
     */
    public static void setPosRotScale(Vector3f position, Quaternionf rotation, Vector3f scale){
        setModel(new Matrix4f().translate(position).rotate(rotation).scale(scale));
    }

    public static void setModel(Matrix4f model){
        setUniform("model", model);
    }
    
    public static void setTimeMod(float mod){
        setUniform("time", mod);
    }
    
    public static void setDistanceField(int distfield){
        setUniform("impl", distfield);
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
    
    public static void setUVCoordinateMultiplierX(float f){
        setUniform("uvmultx", f);
    }
    
    public static void setUVCoordinateMultiplierY(float f){
        setUniform("uvmulty", f);
    }
    
    public static void setInstanced(boolean instanced){
        setUniform("inst", instanced);
    }
    
    public static void passMaterial(Material m){
        setUniform("material.ns", (float) m.nsExponent);
        setUniform("material.ka",m.ka);
        setUniform("material.kd", m.kd);
        setUniform("material.ks", m.ks);
        setUniform("material.hasspec", m.hasspecmap);
        setUniform("material.hasspecpow", m.hasspecpow);
        setUniform("material.hasnormmap", m.hasnormmap);
        setUniform("material.hasambmap", m.hasreflmap);
        setUniform("material.hascolormap", m.hascolmap);
        setUniform("material.hasem", m.hasemm);
    }
    
    public static void setBillBoard(int yes){  
        setUniform("billboard", yes);
    }
    
    /**
     * Enables the given vertex attribute in the current vertex shader
     * @param loc Attribute to enable
     */
    public static void enableVertexAttribute(String loc){
        programs.values().stream().filter((p) -> (p.getType() == ShaderProgram.ShaderType.VERTEX)).forEach((p) -> p.enableVertexAttribute(attributeLocations.get(loc)));
    }
    
    /**
     * Disables the given vertex attribute in the current vertex shader
     * @param loc Attribute to disable
     */
    public static void disableVertexAttribute(String loc){
        programs.values().stream().filter((p) -> (p.getType() == ShaderProgram.ShaderType.VERTEX)).forEach((p) -> p.disableVertexAttribute(attributeLocations.get(loc)));
    }

    public static int getVertexAttributeIndex(String loc){
        return attributeLocations.get(loc);
    }
    
    /**
     * Finds and saves the location of the uniform with name {@code loc} in every attached shader
     * @param loc Name of uniform
     */
    public static void findUniform(String loc){
        //if(searchedUniforms.contains(loc))
        //    return;
        for(ShaderProgram p : programs.values()){
            p.findUniformLocation(loc);
        }
        searchedUniforms.add(loc);
    }

    public static boolean checkCurrentUniform(String name, Object val){
        if(currentUniforms.get(name) != null && currentUniforms.get(name).equals(val)) return true;
        currentUniforms.put(name, val);
        return false;
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     * Note, this will only work if the uniform named {@code name} has already been searched for using {@link #findUniform(String)},
     * otherwise no change will occur in any shader
     * @param name Name of uniform
     * @param val New value of uniform
     */
    public static void setUniform(String name, Vector3f val){
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
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
        if(checkCurrentUniform(name, val)) return;
        for(ShaderProgram p : programs.values()){
            int loc = p.getUniformLocation(name);
            if(loc >= 0)
                p.setUniform(loc, val);
        }
    }

    /**
     * Binds the sampler named {@code name} to texture location {@code loc} in every fragment shader
     * @param name Name of sampler in shader
     * @param loc New location of sampler
     */
    public static void setTextureLocation(String name, int loc){
        programs.values().stream().filter((p) -> (p.getType() == ShaderProgram.ShaderType.FRAGMENT)).forEach((p) -> p.setUniform(p.getUniformLocation(name), loc));
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
     * @param ubo UBO to be bound, as a {@link GraphicsBuffer}
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
        st += vert.getId()
                + ";";

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
        ShaderProgram tescprogram = programs.get(tesc);
        ShaderProgram teseprogram = programs.get(tese);
        ShaderProgram geomprogram = programs.get(geom);
        ShaderProgram fragprogram = programs.get(frag);
        
        String id = getUniqueConfigID(vertprogram, tescprogram, teseprogram, geomprogram, fragprogram);
        realConfigurationNames.put(name, id);
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
        if(currentShader.equals(name)) return;

        String id = realConfigurationNames.get(name);
        ShaderPipeline pipeline = pipelines.get(id);

        if(pipeline == null){
            throw new ShaderException("Failed to find pipeline named " + name);
        }

        pipeline.bind();

        currentShader = name;

        currentvert = pipeline.getShader(ShaderType.VERTEX);
        currenttesc = pipeline.getShader(ShaderType.TESS_CONTROL);
        currenttese = pipeline.getShader(ShaderType.TESS_EVAL);
        currentgeom = pipeline.getShader(ShaderType.GEOMETRY);
        currentfrag = pipeline.getShader(ShaderType.FRAGMENT);
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
        return currentShader;
    }

    public static ShaderProgram loadShader(String name, String loc){

        ShaderType type;
        String ending = loc.substring(loc.lastIndexOf(".") + 1);
        switch(ending){
            case "vert":
                type = ShaderType.VERTEX;
                break;
            case "tesc":
                type = ShaderType.TESS_CONTROL;
                break;
            case "tese":
                type = ShaderType.TESS_EVAL;
                break;
            case "geom":
                type = ShaderType.GEOMETRY;
                break;
            case "frag":
                type = ShaderType.FRAGMENT;
                break;
            default:
                return null;
        }

        return loadShader(name, loc, type);
    }

    public static ShaderProgram loadShader(String name, String loc, ShaderType type){
        try {
            String sec = FileStringLoader.loadStringSequence(URLDecoder.decode(loc, StandardCharsets.UTF_8));
            return createShader(name, type, sec);
        } catch (UnsupportedEncodingException ex) {
            GGConsole.error("Failed to load shader: " + name);
            return null;
        } catch (IOException ex) {
            GGConsole.error("Failed to find shader file for " + loc);
            return null;
        }
    }

    public static ShaderProgram createShader(String name,  ShaderType type, String source){
        var program = ShaderProgram.create(type, source, name);

        for(String s : searchedUniforms){
            program.findUniformLocation(s);
        }

        program.checkStatus();
        return program;
    }

    private static void loadShaderFiles(){
        var dir = new File(Resource.getAbsoluteFromLocal("\\resources\\glsl\\"));
        var allfiles = dir.list();

        var filesToProcess = Arrays.stream(allfiles)
                .filter(not(programs::containsKey))
                .filter(not("error.glsl"::equals))
                .collect(Collectors.toList());

        if(filesToProcess.stream().allMatch(f -> ShaderFile.getType(f) == ShaderFile.ShaderFileType.UTIL)){
            GGConsole.log("No new/modified shaders were found, using cache");
            return;
        }

        shaderfiles.putAll(filesToProcess
                .stream()
                .unordered()
                //.parallel()
                .map(ShaderFile::new)
                .filter(ShaderFile::isParsed)
                .collect(Collectors.toList())
                .stream()
                .peek(ShaderController::processParsedShader)
                .peek(ShaderFile::compile)
                .collect(Collectors.toMap(ShaderFile::getName, shader -> shader)));

        GGConsole.log("Detected changes/additions to cached shaders: " + shaderfiles.keySet());
    }

    private static void processParsedShader(ShaderFile file){
        if(file.getType() == ShaderFile.ShaderFileType.VERT || file.getType() == ShaderFile.ShaderFileType.UTIL){
            var wholeFile = file.getTree().getAll();
            var inputs = wholeFile.stream()
                    .filter(n -> n instanceof Parser.Declaration)
                    .map(n -> (Parser.Declaration) n)
                    .filter(n -> n.modifiers.modifiers.stream()
                                .map(i -> i.value)
                                .anyMatch(s -> s.equals("in")))
                    .collect(Collectors.toList());
            for(var in : inputs){
                if(!attributeLocations.containsKey(in.name.value)){
                    attributeLocations.put(in.name.value, attributeCounter);
                    attributeCounter++;
                }
                in.modifiers.modifiers.add(0, new Parser.Identifier("layout(location = " + attributeLocations.get(in.name.value) + ")"));
            }
        }
    }

    private static void linkShaders(){
        var processing = shaderfiles.entrySet().stream()
                .unordered()
                .parallel()
                .filter(entry -> !entry.getValue().getType().equals(ShaderFile.ShaderFileType.UTIL))
                .map(entry -> new ShaderFileHolder(entry.getKey(), entry.getValue()))
                .peek(ShaderFileHolder::link)
                .peek(ShaderController::dumpShader)
                .collect(Collectors.toList());

        completedfiles.addAll(processing);
    }

    private static void dumpShader(ShaderFileHolder holder){
    }

    private static void createGLShaderFromFile(){
        for(var entry : completedfiles){
            String source = entry.fulldata;
            String name = entry.name;
            ShaderType ntype = ShaderType.fromFileType(entry.type);

            try{
                var program = createShader(name, ntype, source);
                cacheShader(entry, program);
                programs.put(name, program);
            }catch(ShaderException e){
                try{
                    var errorfile = new File(Resource.getAbsoluteFromLocal("\\resources\\glsl\\error.glsl"));
                    errorfile.createNewFile();

                    PrintWriter writer = new PrintWriter(errorfile);
                    writer.println("COMPILED GGSL ERROR SOURCE: " + e.getMessage().trim());
                    writer.print(source);
                    writer.flush();
                }catch(IOException e1){
                    e1.printStackTrace();
                }

                var ne = new ShaderException(e.getMessage());
                ne.setStackTrace(e.getStackTrace());
                GGConsole.exception(ne);
            }
        }
    }


    private static void cacheShader(ShaderFileHolder holder, ShaderProgram compiled) {
        try {
            var cacheFile = new File(Resource.getAbsoluteFromLocal("/internal/cache/" + holder.source.getName() + ".bscf"));
            cacheFile.getParentFile().mkdirs();

            try(var out = new GGOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)))){
                out.write(SystemInfo.get("Graphics Renderer"));
                out.write(SystemInfo.get("Graphics Vendor"));
                out.write(SystemInfo.get("Internal GL Version"));
                out.write(SystemInfo.get("Java Version"));

                out.write(attributeLocations.size());
                for(var attrib : attributeLocations.entrySet()){
                    out.write(attrib.getKey());
                    out.write(attrib.getValue());
                }

                out.write(holder.source.getName());

                var hashLong = HashUtil.getMeowHash(Resource.getShaderPath(holder.source.getName()));
                out.write(hashLong);

                out.write(holder.dependencies.size());
                for(var dependency : holder.dependencies){
                    out.write(dependency.getName());
                    var dependencyHashLong = HashUtil.getMeowHash(Resource.getShaderPath(dependency.getName()));

                    out.write(dependencyHashLong);
                }

                var compiledData = compiled.getProgramBinary();
                out.write(compiledData.limit());
                out.write(GGBufferUtils.get(compiledData));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadCachedShaders() {
        if(!loadFromCache) return;
        var path = Resource.getAbsoluteFromLocal("\\internal\\cache\\");
        var dir = new File(path);
        var allfiles = dir.list();
        if(allfiles == null) return;
        Arrays.stream(allfiles)
                .map(s -> path + "\\" + s)
                .forEach(ShaderController::parseCachedShader);
    }

    private static void parseCachedShader(String path) {
        try(var in = new GGInputStream(new BufferedInputStream(new FileInputStream(path)))){
            var renderer = in.readString();
            var vendor = in.readString();
            var version = in.readString();
            var java = in.readString();

            if(!renderer.equals(SystemInfo.get("Graphics Renderer"))) return;
            if(!vendor.equals(SystemInfo.get("Graphics Vendor"))) return;
            if(!version.equals(SystemInfo.get("Internal GL Version"))) return;
            if(!java.equals(SystemInfo.get("Java Version"))) return;

            var attributeAmount = in.readInt();
            for(int i = 0; i < attributeAmount; i++){
                var attribName = in.readString();
                var attribId = in.readInt();
                if(attributeLocations.get(attribName) != null && attributeLocations.get(attribName) != attribId){
                    return;
                }
                attributeLocations.put(attribName, attribId);
            }

            var shaderName = in.readString();
            var mainShaderHash = in.readLong();
            var currentMainShaderHash = HashUtil.getMeowHash(Resource.getShaderPath(shaderName));
            if(mainShaderHash != currentMainShaderHash) return;

            var dependencyCount = in.readInt();
            for(int i = 0; i < dependencyCount; i++){
                var dependencyName = in.readString();
                var dependencyHash = in.readLong();

                var currentDependencyHash = HashUtil.getMeowHash(Resource.getShaderPath(dependencyName));
                if(dependencyHash != currentDependencyHash) return;
            }
            //all checks passed

            var binarySize = in.readInt();
            var binary = Allocator.alloc(binarySize).put(in.readByteArray(binarySize)).flip();

            var program = ShaderProgram.createFromBinary(ShaderType.fromFileType(ShaderFile.getType(shaderName)), binary, shaderName);

            programs.put(shaderName, program);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void regenerateShaderController(){

        GGConsole.log("Reloading shaders...");

        loadShaders();

        setDefaultPipelines();

        setUniforms();

        checkError();
    }

    private ShaderController() {
    }

    private static class ShaderFileHolder{
        ShaderFile.ShaderFileType type;
        List<ShaderFile> dependencies = new ArrayList<>();
        String name;
        ShaderFile source;
        String fulldata;

        List<String> glvals = new ArrayList<>();

        public ShaderFileHolder(String name, ShaderFile source){
            this.type = source.getType();
            this.name = name;
            this.source = source;
            source.getIncludes().stream()
                    .map(shaderfiles::get)
                    .forEach(this::addDependency);
        }

        public void link(){
            fulldata = source.getCompiledSource();
            for(var file : dependencies){
                fulldata = file.getCompiledSource() + fulldata;
            }

            fulldata = "#version " + source.getVersion().replace(".", "").concat("0\n")  + fulldata;
        }

        private void addDependency(ShaderFile file){
            if(!dependencies.contains(file)){
                dependencies.add(file);
                try {
                    file.getIncludes().stream()
                            .map(shaderfiles::get)
                            .forEach(this::addDependency);
                }catch (NullPointerException e){
                    GGConsole.exception(new ShaderException("Failed to load dependency for " + this.name, e));
                    throw new ShaderException(e);
                }
            }else{
                dependencies.remove(file);
                dependencies.add(file);
            }
        }
    }
}
