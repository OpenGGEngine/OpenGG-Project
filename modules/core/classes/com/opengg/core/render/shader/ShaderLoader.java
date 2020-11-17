package com.opengg.core.render.shader;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.render.shader.ggsl.ShaderFile;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ShaderLoader {
    private final Map<String, ShaderFile> shaderFiles = new HashMap<>();
    private final List<ShaderFileHolder> completedFiles = new ArrayList<>();

    private final Map<String, ShaderProgram> shaders = new HashMap<>();

    private final boolean loadFromCache = false;

    private final String path;

    public ShaderLoader(String shaderPath){
        this.path = shaderPath;
    }

    public Map<String, ShaderProgram> loadShaders(){
        long time = System.currentTimeMillis();

        GGConsole.log("Loading shaders...");
        //loadCachedShaders();
        loadShaderFiles();
        linkShaders();
        createGLShaderFromFile();

        long finaltime = System.currentTimeMillis() - time;

        GGConsole.log("Loaded shaders in " + finaltime + " milliseconds");

        return shaders;
    }


    private void loadShaderFiles(){
        var dir = new File(Resource.getAbsoluteFromLocal(path));
        var allfiles = dir.list();

        var filesToProcess = Arrays.stream(allfiles)
                .filter(not(ShaderController::isShaderLoaded))
                .filter(not("error.glsl"::equals))
                .collect(Collectors.toList());

        if(filesToProcess.stream().allMatch(f -> ShaderFile.getType(f) == ShaderFile.ShaderFileType.UTIL)){
            GGConsole.log("No new/modified shaders were found, using cache");
            return;
        }
        shaderFiles.putAll(filesToProcess
                .stream()
                .unordered()
                .parallel()
                .map(ShaderFile::new)
                .filter(ShaderFile::isParsed)
                .collect(Collectors.toList())
                .stream()
                .peek(ShaderController::processPrecompiledShader)
                .peek(ShaderFile::compile)
                .collect(Collectors.toMap(ShaderFile::getName, shader -> shader)));

        //GGConsole.log("Detected changes/additions to cached shaders: " + shaderFiles.keySet());
    }

    private void linkShaders(){
        var processing = shaderFiles.entrySet().stream()
                .unordered()
                .parallel()
                .filter(entry -> !entry.getValue().getType().equals(ShaderFile.ShaderFileType.UTIL))
                .map(entry -> new ShaderFileHolder(entry.getKey(), entry.getValue()))
                .peek(ShaderFileHolder::link)
                .peek(this::dumpShader)
                .collect(Collectors.toList());

        completedFiles.addAll(processing);
    }

    private void dumpShader(ShaderFileHolder holder){
        /*System.out.println(holder.name.toUpperCase() + "============================");
        System.out.println(holder.compiledShader);*/
    }

    private void createGLShaderFromFile(){
        for(var file : completedFiles){
            String source = file.compiledShader;
            String name = file.name;
            try{
                var shader = ShaderController.createShader(name, ShaderProgram.ShaderType.fromFileType(file.type), source, file.uniforms);
                //cacheShader(entry, program);

                shaders.put(name, shader);
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

                GGConsole.exception(e);
            }
        }
    }
/*
    private void cacheShader(ShaderFileHolder holder, ShaderProgram compiled) {
        try {
            var cacheFile = new File(Resource.getAbsoluteFromLocal("/internal/cache/" + holder.source.getName() + ".bscf"));
            cacheFile.getParentFile().mkdirs();

            try(var out = new GGOutputStream(new BufferedOutputStream(new FileOutputStream(cacheFile)))){
                out.write(SystemInfo.get("Renderer"));
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

    private void loadCachedShaders() {
        if(!loadFromCache) return;
        var path = Resource.getAbsoluteFromLocal("\\internal\\cache\\");
        var allfiles = new File(path).list();
        if(allfiles == null) return;
        Arrays.stream(allfiles)
                .map(s -> path + "\\" + s)
                .forEach(ShaderLoader::parseCachedShader);
    }


    private void parseCachedShader(String path) {
        try(var in = new GGInputStream(new BufferedInputStream(new FileInputStream(path)))){
            var renderer = in.readString();
            var rendererProvider = in.readString();
            var vendor = in.readString();
            var version = in.readString();
            var java = in.readString();

            if(!renderer.equals(SystemInfo.get("Renderer"))) return;
            if(!rendererProvider.equals(SystemInfo.get("Graphics Renderer"))) return;
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

            var program = ShaderProgram.createFromBinary(ShaderProgram.ShaderType.fromFileType(ShaderFile.getType(shaderName)), binary, shaderName);

            programs.put(shaderName, program);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/


    private class ShaderFileHolder{
        public ShaderFile.ShaderFileType type;
        private final List<ShaderFile> dependencies = new ArrayList<>();
        public List<ShaderController.Uniform> uniforms = new ArrayList<>();
        public String name;
        public ShaderFile source;
        public String compiledShader;

        public ShaderFileHolder(String name, ShaderFile source){
            this.type = source.getType();
            this.name = name;
            this.source = source;
            source.getIncludes().stream()
                    .map(shaderFiles::get)
                    .forEach(this::addDependency);
        }

        public void link(){
            compiledShader = source.getCompiledSource();
            uniforms = new ArrayList<>(source.getUniforms());
            for(var file : dependencies){
                compiledShader = file.getCompiledSource() + compiledShader;
                uniforms.addAll(file.getUniforms());
            }

            compiledShader = """
            #version %s
            #extension GL_ARB_explicit_uniform_location : require
            
            %s;
            """.formatted(source.getVersion().replace(".", "").concat("0"), compiledShader);
        }

        private void addDependency(ShaderFile file){
            if(!dependencies.contains(file)){
                dependencies.add(file);
                try {
                    file.getIncludes().stream()
                            .map(shaderFiles::get)
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
