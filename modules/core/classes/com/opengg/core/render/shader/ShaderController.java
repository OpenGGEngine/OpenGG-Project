/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.shader;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.exceptions.ShaderException;
import com.opengg.core.math.*;
import com.opengg.core.render.GraphicsBuffer;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.shader.OpenGLShaderPipeline;
import com.opengg.core.render.internal.opengl.shader.OpenGLShaderProgram;
import com.opengg.core.render.internal.opengl.texture.OpenGLTexture;
import com.opengg.core.render.internal.vulkan.VulkanBuffer;
import com.opengg.core.render.internal.vulkan.VulkanDescriptorSet;
import com.opengg.core.render.internal.vulkan.VulkanRenderer;
import com.opengg.core.render.internal.vulkan.shader.VulkanPipelineCache;
import com.opengg.core.render.internal.vulkan.shader.VulkanShaderPipeline;
import com.opengg.core.render.internal.vulkan.texture.VulkanImage;
import com.opengg.core.render.shader.ggsl.Parser;
import com.opengg.core.render.shader.ggsl.ShaderFile;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowOptions;
import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static com.opengg.core.render.shader.ShaderController.DescriptorType.COMBINED_TEXTURE_SAMPLER;
import static com.opengg.core.render.shader.ShaderController.DescriptorType.UNIFORM_BUFFER;
import static com.opengg.core.render.shader.ShaderProgram.ShaderType;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_ASPECT_COLOR_BIT;

/**
 * Controller/manager for GLSL shaders
 *
 * @author Javier
 */
public class ShaderController {
    private static int attributeCounter = 0;
    private static int uniformCounter = 0;
    private static int setCounter = 8; // no conflict with preset sets

    private static Map<String, ShaderProgram> programs = new HashMap<>();
    private static final Map<String, ShaderPipeline> pipelines = new HashMap<>();
    private static final Map<String, String> pipelineNames = new HashMap<>();

    private static final Map<String, Integer> attributeLocations = new HashMap<>();

    private static final Map<String, UniformPosition> uniformDescriptorPositions = new HashMap<>();
    private static final Map<UniformPosition, UniformContainer> currentUniforms = new HashMap<>();
    private static final List<Integer> editedSets = new ArrayList<>();

    private static final Map<String, Integer> openGlUniformMapping = new HashMap<>();

    private static final Map<String, Integer> setMappings = new HashMap<>();
    private static final Map<String, Integer> bindingMappings = new HashMap<>();
    private static final Map<DescriptorPosition, ByteBuffer> currentDescriptorBindingValue = new HashMap<>();
    private static final Map<DescriptorPosition, Texture> currentDescriptorBindingImageValue = new HashMap<>();


    private static final Set<String> searchedUniforms = new HashSet<>();

    private static String currentPipelineName = "";
    private static ShaderPipeline currentPipeline;

    private static int currentBind = 0;

    private static Texture defaultVulkanTexture;

    private static final Map<String, Integer> glslTypeSizes = Map.ofEntries(
            Map.entry("double", 8),
            Map.entry("float", 4),
            Map.entry("vec2", 4 * 2),
            Map.entry("vec3", 4 * 3),
            Map.entry("vec4", 4 * 4),
            Map.entry("int", 4),
            Map.entry("uint", 4),
            Map.entry("ivec2", 4 * 2),
            Map.entry("ivec3", 4 * 3),
            Map.entry("ivec4", 4 * 4),
            Map.entry("mat4", 4 * 16),
            Map.entry("mat3", 4 * 9)
    );

    /**
     * Initializes the controller and loads all default shaders
     */
    public static void initialize() {
        GGConsole.log("Shader Controller initializing...");

        /* Set shader variables */

        programs = new ShaderLoader("resources/glsl/").loadShaders();
        generateCommonPipelines();
        setUniforms();

        GGConsole.log("Shader Controller initialized, loaded " + programs.size() + " shader programs");
    }

    private static void generateCommonPipelines() {

        use("object.vert", "object.frag");
        saveCurrentConfiguration("object");

        use("object.vert", "texture.frag");
        saveCurrentConfiguration("texture");

        //use("object.vert", "material.frag");
        //saveCurrentConfiguration("material");

        // use("anim2.vert", "object.frag");
        // saveCurrentConfiguration("animation2");

        //use("tangent.vert", "tangent.frag");
        //saveCurrentConfiguration("tangent");

        use("instance.vert", "object.frag");
        saveCurrentConfiguration("instance");

        //  use("object.vert", "terrainmulti.frag");
        // saveCurrentConfiguration("terrain");

        //   use("object.vert", "water.frag");
        //   saveCurrentConfiguration("water");

        //   use("object.vert", "ssao.frag");
        //   saveCurrentConfiguration("ssao");

        use("object.vert", "hdr.frag");
        saveCurrentConfiguration("hdr");

        //   use("object.vert", "bright.frag");
        //  saveCurrentConfiguration("bright");

        //  use("object.vert", "arraytex.frag");
        //  saveCurrentConfiguration("array");

        use("object.vert", "passthrough.frag");
        saveCurrentConfiguration("no_fragment");

        use("noprocess.vert", "passthrough.frag");
        saveCurrentConfiguration("no_process");

        //   use("modeltransform.vert", "point.geom", "point.frag");
        //   saveCurrentConfiguration("pointshadow");

        //   use("object.vert", "cubemap.frag");
        //   saveCurrentConfiguration("sky");

        //use("object.vert", "fxaa.frag");
        // saveCurrentConfiguration("fxaa");

        use("object.vert", "text.frag");
        saveCurrentConfiguration("sdf");

        use("object.vert", "color_alpha.frag");
        saveCurrentConfiguration("ttf");

        //    use("object.vert", "cuboid_scaling.frag");
        //    saveCurrentConfiguration("cuboid");

        use("object.vert", "gui.frag");
        saveCurrentConfiguration("gui");

        //   use("object.vert", "bar.frag");
        //    saveCurrentConfiguration("bar");

        use("object.vert", "add.frag");
        saveCurrentConfiguration("add");

        //  use("object.vert", "gaussh.frag");
        //  saveCurrentConfiguration("blurh");

        //  use("object.vert", "gaussv.frag");
        //   saveCurrentConfiguration("blurv");

        use("particle.vert", "texture.frag");
        saveCurrentConfiguration("particle");

        GGConsole.log("Created " + pipelines.size() + " default shader pipelines");
    }

    private static void setUniforms() {
        /*//setUniform("jointsMatrix", new Matrix4f[200]);
        setUniform("projection", new Matrix4f());
        setUniform("divAmount", 1f);
        setUniform("percent", 1f);*/
        setUniform("shadow", 1);
        /*setUniform("fill", new Vector3f());
        setUniform("back", new Vector3f());
        setUniform("rot", new Vector3f(0,0,0));
        setUniform("scale", new Vector3f(1,1,1));
        setUniform("camera", new Vector3f(0,0,0));
        setUniform("color", new Vector3f(1,1,1));
        setUniform("numLights", 1);
        setUniform("invertMultiplier", 1);
        setUniform("layer", 0);*/
        //setUniform("billboard", false);
        setUniform("exposure", 1f);
        setUniform("gamma", 2.2f);

        // setMatLinks();
    }


    public static int getVertexAttributeIndex(String loc) {
        return attributeLocations.get(loc);
    }

    public static boolean isAlready(UniformPosition position, UniformContainer val) {
        if (currentUniforms.get(position) != null && currentUniforms.get(position).equals(val)) return true;
        currentUniforms.put(position, val);
        return false;
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     */
    public static void setUniform(String name, ByteBuffer val) {
        setUniform(name, new UniformContainer.ByteBufferContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, Vector3f val) {
        setUniform(name, new UniformContainer.Vector3fContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, Vector2f val) {
        setUniform(name, new UniformContainer.Vector2fContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, Matrix4f val) {
        setUniform(name, new UniformContainer.Matrix4fContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, Matrix4f[] val) {
        throw new UnsupportedOperationException();
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, int val) {
        setUniform(name, new UniformContainer.IntContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, float val) {
        setUniform(name, new UniformContainer.FloatContainer(val));
    }

    /**
     * Sets the value in the uniform named {@code name} to {@code val} in every shader<br><br>
     *
     * @param name Name of uniform
     * @param val  New value of uniform
     */
    public static void setUniform(String name, boolean val) {
        setUniform(name, new UniformContainer.IntContainer(val ? 1 : 0));
    }


    public static void setUniform(String name, Texture texture) {
        setUniform(name, new UniformContainer.TextureContainer(texture));
    }

    public static void setUniform(String name, UniformContainer val) {
        //if(val.toString().contains("null")) throw new NullPointerException("Shader parameter is null.");
        var pos = uniformDescriptorPositions.get(name);

        if (isAlready(pos, val)) return;
        switch (RenderEngine.getRendererType()) {
            case OPENGL -> currentUniforms.put(pos, val);
            case VULKAN -> {
                if (val instanceof UniformContainer.TextureContainer textureContainer) {
                    currentDescriptorBindingImageValue.put(pos.descriptor, textureContainer.contents());
                } else {
                    var buffer = currentDescriptorBindingValue.get(pos.descriptor);
                    if (buffer.capacity() < val.getBuffer().capacity()) {
                        currentDescriptorBindingValue.put(pos.descriptor, val.getBuffer());
                    } else if (buffer == val.getBuffer()) {
                        currentDescriptorBindingValue.put(pos.descriptor, val.getBuffer());
                    } else {
                        buffer.position(pos.offset).put(val.getBuffer());
                        buffer.rewind();
                        currentDescriptorBindingValue.put(pos.descriptor, buffer);

                    }
                }
            }
        }

        if (!editedSets.contains(pos.descriptor.set)) editedSets.add(pos.descriptor.set);
    }

    /**
     * Returns a unique positive location for a uniform buffer object
     *
     * @return New location
     */
    public static int getUniqueUniformBufferLocation() {
        int n = currentBind;
        currentBind++;
        return n;
    }

    /**
     * Binds the given uniform buffer object to the given GLSL identifier
     *
     * @param ubo  UBO to be bound, as a {@link GraphicsBuffer}
     * @param name Name of buffer in GLSL to bind UBO to
     */
    public static void setUniformBlockLocation(GraphicsBuffer ubo, String name) {
        setUniformBlockLocation(ubo.getBase(), name);
    }

    /**
     * Binds the given uniform buffer object ID to the given GLSL identifier
     *
     * @param id   UBO ID to be bound
     * @param name Name of buffer in GLSL to bind UBO to
     */
    public static void setUniformBlockLocation(int id, String name) {
        for (ShaderProgram p : programs.values()) {
            p.setUniformBlockIndex(id, name);
        }
    }

    public static VulkanDescriptorSet generateSetFromCurrentValues(int set, VulkanShaderPipeline pipeline) {
        var setBindings = pipeline.getUsedLayouts()[set];
        var imageDescriptors = setBindings.bindingList().stream().filter(b -> b.type() == COMBINED_TEXTURE_SAMPLER).collect(Collectors.toList());
        var bufferDescriptors = setBindings.bindingList().stream().filter(b -> b.type() == UNIFORM_BUFFER).collect(Collectors.toList());

        var descriptorSet = new VulkanDescriptorSet(pipeline.getUsedLayouts()[set]);
        for (var image : imageDescriptors) {
            var vkImage = (VulkanImage) currentDescriptorBindingImageValue.get(new DescriptorPosition(set, image.binding()));
            descriptorSet.setDescriptorSetContents(vkImage.getImageView(VK_IMAGE_ASPECT_COLOR_BIT), vkImage.getSampler(), image.binding());
        }

        for (var buffer : bufferDescriptors) {
            var buffContents = currentDescriptorBindingValue.get(new DescriptorPosition(set, buffer.binding()));
            if (buffContents.capacity() == 0) continue;
            var buf = (VulkanBuffer) GraphicsBuffer.allocate(GraphicsBuffer.BufferType.UNIFORM_BUFFER, buffContents, GraphicsBuffer.UsageType.NONE);
            descriptorSet.setDescriptorSetContents(buf, 0, buffer.binding());
        }

        return descriptorSet;
    }

    public static void uploadModifiedDescriptorSets() {
        switch (RenderEngine.getRendererType()) {
            case OPENGL -> {
                for (var currentUniform : currentUniforms.entrySet()) {
                    if (editedSets.contains(currentUniform.getKey().descriptor.set)) {
                        var uniform = currentUniform.getValue();
                        var shaderList = new ArrayList<>(List.of(currentPipeline.getShader(ShaderType.VERTEX), currentPipeline.getShader(ShaderType.FRAGMENT)));
                        if (currentPipeline.getShader(ShaderType.GEOMETRY) != null)
                            shaderList.add(currentPipeline.getShader(ShaderType.GEOMETRY));

                        for (var shader : shaderList) {
                            if (((OpenGLShaderProgram) shader).uniformSet.contains(currentUniform.getKey())) {
                                if (uniform instanceof UniformContainer.IntContainer container) {
                                    shader.setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.FloatContainer container) {
                                    shader.setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.DoubleContainer container) {
                                    //currentPipeline.getShader(ShaderType.VERTEX).setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Vector2fContainer container) {
                                    shader.setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Vector3fContainer container) {
                                    shader.setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Vector4fContainer container) {
                                    //currentPipeline.getShader(ShaderType.VERTEX).setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Vector2iContainer container) {
                                    //currentPipeline.getShader(ShaderType.VERTEX).setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Vector3iContainer container) {
                                    //currentPipeline.getShader(ShaderType.VERTEX).setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Matrix3fContainer container) {
                                    //currentPipeline.getShader(ShaderType.VERTEX).setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.Matrix4fContainer container) {
                                    shader.setUniform(currentUniform.getKey().descriptor.set, container.contents());
                                } else if (uniform instanceof UniformContainer.TextureContainer container) {
                                    ((OpenGLTexture) container.contents()).useAtLocation(currentUniform.getKey().descriptor.set);
                                }
                            }
                        }
                    }
                }
            }
            case VULKAN -> {
                for (int i = 0; i < ((VulkanShaderPipeline) currentPipeline).getUsedLayouts().length; i++) {
                    if (editedSets.contains(i) && !((VulkanShaderPipeline) currentPipeline).getUsedLayouts()[i].bindingList().isEmpty()) {
                        var newSet = generateSetFromCurrentValues(i, (VulkanShaderPipeline) currentPipeline);
                        VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindDescriptorSets(VulkanRenderer.getRenderer().getCurrentPipeline(), i, newSet);
                    }
                }
            }
        }
        //System.out.println(GL11.glGetError());
        editedSets.clear();
    }

    private static String getConfigurationID(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag) {
        String st = "";
        st += vert.getName()
                + ";";

        if (tesc != null)
            st += tesc.getName();
        st += ";";

        if (tese != null)
            st += tese.getName();
        st += ";";

        if (geom != null)
            st += geom.getName();
        st += ";";

        st += frag.getName();
        return st;
    }

    /**
     * Activates the given vertex, geometry, and fragment shaders for rendering<br><br>
     * <p>
     * This method searches for a shader pipeline that already uses this specific combination of shaders, and uses it if available.
     * Otherwise, it creates a new pipeline, saves it, and uses it
     *
     * @param vert Vertex shader to use
     * @param geom Geometry shader to use
     * @param frag Fragment shader to use
     */
    public static void use(String vert, String geom, String frag) {
        use(programs.get(vert), null, null, programs.get(geom), programs.get(frag));
    }

    /**
     * Activates the given vertex and fragment shadersfor rendering<br><br>
     * <p>
     * This method searches for a shader pipeline that already uses this specific combination of shaders, and uses it if available.
     * Otherwise, it creates a new pipeline, saves it, and uses it
     *
     * @param vert Vertex shader to use
     * @param frag Fragment shader to use
     */
    public static void use(String vert, String frag) {
        use(programs.get(vert), null, null, null, programs.get(frag));
    }

    private static void use(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag) {
        String id = getConfigurationID(vert, tesc, tese, geom, frag);

        ShaderPipeline pipeline;
        if ((pipeline = pipelines.get(id)) == null) {
            pipeline = ShaderPipeline.create(vert, tesc, tese, geom, frag);
            pipelines.put(id, pipeline);
        }
        pipeline.use();

        currentPipeline = pipeline;
    }

    /**
     * Saves the current shader configuration with a given readable name for easier reuse
     *
     * @param name Name of configuration
     */
    public static void saveCurrentConfiguration(String name) {
        ShaderProgram vertProgram = currentPipeline.getShader(ShaderType.VERTEX);
        ShaderProgram tescProgram = currentPipeline.getShader(ShaderType.TESS_CONTROL);
        ShaderProgram teseProgram = currentPipeline.getShader(ShaderType.TESS_EVAL);
        ShaderProgram geomProgram = currentPipeline.getShader(ShaderType.GEOMETRY);
        ShaderProgram fragProgram = currentPipeline.getShader(ShaderType.FRAGMENT);

        String id = getConfigurationID(vertProgram, tescProgram, teseProgram, geomProgram, fragProgram);
        pipelineNames.put(name, id);
    }

    /**
     * Uses the configuration with the given name<br><br>
     * <p>
     * This name should be the same as the one previously saved in {@link #saveCurrentConfiguration(String)}
     *
     * @param name Name of configuration
     */
    public static void useConfiguration(String name) {
        if (currentPipelineName.equals(name)) return;

        var pipeline = pipelines.get(pipelineNames.get(name));
        if (pipeline == null) {
            throw new ShaderException("Failed to find pipeline named " + name);
        }

        editedSets.clear();
        switch (RenderEngine.getRendererType()) {
            case OPENGL -> {
                pipeline.use();
                editedSets.addAll(((OpenGLShaderPipeline) pipeline).getAllUsedUniforms().stream().map(u -> u.position.descriptor().set).collect(Collectors.toList()));
            }
            case VULKAN -> {
                var fullPipeline = VulkanPipelineCache.getPipeline(
                        VulkanRenderer.getRenderer().getCurrentPipeline().getFormat().setShaders((VulkanShaderPipeline) pipeline));
                VulkanRenderer.getRenderer().getCurrentCommandBuffer().bindPipeline(fullPipeline);
                var usedLayouts = ((VulkanShaderPipeline) pipeline).getUsedLayouts();
                for (int i = 0; i < usedLayouts.length; i++) {
                    if (!usedLayouts[i].bindingList().isEmpty()) editedSets.add(i);
                }
            }
        }
        currentPipelineName = name;
        currentPipeline = pipeline;
    }

    public static void clearPipelineCache() {
        for (ShaderPipeline p : pipelines.values()) {
            p.delete();
        }
        pipelines.clear();
    }

    public static String getCurrentConfigurationName() {
        return currentPipelineName;
    }

    public static ShaderPipeline getConfiguration(String name) {
        return pipelines.get(pipelineNames.get(name));
    }

    public static UniformPosition getUniformPosition(String uniform) {
        return uniformDescriptorPositions.get(uniform);
    }

    public static boolean isShaderLoaded(String shader) {
        return programs.containsKey(shader);
    }

    private static void regenerateShaderController() {

        GGConsole.log("Reloading shaders...");

        programs = new ShaderLoader("\\resources\\glsl\\").loadShaders();

        generateCommonPipelines();

        setUniforms();
    }

    static void processPrecompiledShader(ShaderFile file) {
        generateVertexInputPositions(file);


        //process legacy uniforms
        var uniformMaybes = file.getTree().nodes
                .stream()
                .filter(n -> n instanceof Parser.Declaration || n instanceof Parser.Interface)
                .collect(Collectors.toList());
        var declarationUniforms = uniformMaybes.stream()
                .filter(n -> n instanceof Parser.Declaration)
                .map(n -> (Parser.Declaration) n)
                .filter(d -> d.modifiers.modifiers.stream().anyMatch(m -> m.value.equals("uniform")))
                .collect(Collectors.toList());
        var interfaceUniforms = uniformMaybes.stream()
                .filter(n -> n instanceof Parser.Interface)
                .map(n -> (Parser.Interface) n)
                .filter(d -> d.accessor.value.equals("uniform"))
                .collect(Collectors.toList());

       /* uniforms.stream()
                .filter(d -> d.modifiers.modifiers.contains())*/
        //add existing interface buffer bindings to list
        generateMissingInterfaceBindings(interfaceUniforms);

        if (RenderEngine.getRendererType().equals(WindowOptions.RendererType.VULKAN)) {
            preprocessVulkanShader(file, declarationUniforms, interfaceUniforms);
        }

        if (RenderEngine.getRendererType().equals(WindowOptions.RendererType.OPENGL)) {
            preprocessOpenGLShader(file, declarationUniforms, interfaceUniforms);
        }
    }

    private static void generateMissingInterfaceBindings(List<Parser.Interface> interfaceUniforms) {
        for (var iface : interfaceUniforms) {

            var layout = iface.modifiers.modifiers.stream().filter(m -> m instanceof Parser.Layout).map(m -> (Parser.Layout) m).findFirst().get();
            var hasBinding = layout.expressions.stream()
                    .filter(e -> e instanceof Parser.BinaryOp)
                    .map(e -> (Parser.BinaryOp) e)
                    .anyMatch(e -> ((Parser.Identifier) e.left).value.equals("binding"));

            if (hasBinding) {
                int val = layout.expressions.stream()
                        .filter(e -> e instanceof Parser.BinaryOp)
                        .map(e -> (Parser.BinaryOp) e)
                        .filter(b -> b.left instanceof Parser.Identifier i && i.value.equals("binding"))
                        .mapToInt(b -> ((Parser.IntegerLiteral) b.right).value).findFirst().getAsInt();
                bindingMappings.put(iface.name.value, val);
                uniformCounter = Math.max(val + 1, uniformCounter);
            } else {
                if (!bindingMappings.containsKey(iface.name.value))
                    bindingMappings.put(iface.name.value, uniformCounter++);
                var counter = bindingMappings.get(iface.name.value);
                layout.expressions.add(new Parser.BinaryOp(new Parser.Identifier("binding"), new Parser.IntegerLiteral(counter), "="));
            }
        }
    }

    private static void preprocessOpenGLShader(ShaderFile file, List<Parser.Declaration> declarationUniforms, List<Parser.Interface> interfaceUniforms) {
        List<Parser.Declaration> allNewUniforms = new ArrayList<>();
        List<Uniform> processedUniforms = new ArrayList<>();
        for (var interfacee : interfaceUniforms) {
            if (interfacee.modifiers.modifiers.stream().anyMatch(m -> m.value.equals("unwrap"))) {
                var newUniforms = interfacee.declarations.expressions.stream()
                        .map(d -> (Parser.Declaration) d)
                        .peek(d -> d.modifiers.modifiers.add(0, new Parser.Modifier("uniform")))
                        .collect(Collectors.toList());

                allNewUniforms.addAll(newUniforms);

                for (var newUniform : newUniforms) {
                    file.getTree().nodes.add(0, newUniform);
                }

                file.getTree().nodes.remove(interfacee);
            } else {
                interfacee.modifiers.modifiers.stream()
                        .filter(m -> m instanceof Parser.Layout).map(l -> (Parser.Layout) l).findFirst().get()
                        .expressions.removeIf(mod -> mod instanceof Parser.BinaryOp op &&
                        (op.left instanceof Parser.Identifier id && (id.value.equals("set") || id.value.equals("binding"))));

                if (!openGlUniformMapping.containsKey(interfacee.name.value))
                    openGlUniformMapping.put(interfacee.name.value, uniformCounter++);

                var newUnit = openGlUniformMapping.get(interfacee.name.value);

                var bindingOp = new Parser.BinaryOp(new Parser.Identifier("binding"), new Parser.IntegerLiteral(newUnit), "=");
                //Assign buffer unit
                interfacee.modifiers.modifiers.stream()
                        .filter(m -> m instanceof Parser.Layout)
                        .map(m -> (Parser.Layout) m)
                        .findFirst().ifPresentOrElse(
                        l -> l.expressions.add(bindingOp),
                        () -> interfacee.modifiers.modifiers.add(new Parser.Layout(List.of(bindingOp))));
            }
        }

        List<Parser.Declaration> allUniforms = new ArrayList<>(allNewUniforms);
        allUniforms.addAll(declarationUniforms);

        for (var uniform : allUniforms) {
            uniform.modifiers.modifiers.stream().filter(i -> i instanceof Parser.Layout).map(i -> (Parser.Layout) i).findFirst()
                    .ifPresent(l -> uniform.modifiers.modifiers.remove(l));

            if (!openGlUniformMapping.containsKey(uniform.name.value))
                openGlUniformMapping.put(uniform.name.value, uniformCounter++);

            var newUnit = openGlUniformMapping.get(uniform.name.value);

            Parser.BinaryOp bindingOp;

            if (uniform.type.value.contains("sampler")) {
                bindingOp = new Parser.BinaryOp(new Parser.Identifier("binding"), new Parser.IntegerLiteral(newUnit), "=");
            } else {
                bindingOp = new Parser.BinaryOp(new Parser.Identifier("location"), new Parser.IntegerLiteral(newUnit), "=");
            }
            //Assign location
            uniform.modifiers.modifiers.add(new Parser.Layout(List.of(bindingOp)));
            uniformDescriptorPositions.put(uniform.name.value, new UniformPosition(new DescriptorPosition(newUnit, 0), 0));
            processedUniforms.add(new Uniform(uniform.name.value, new UniformPosition(new DescriptorPosition(newUnit, 0), 0),
                    uniform.type.value.contains("sampler") ? COMBINED_TEXTURE_SAMPLER : UNIFORM_BUFFER));
        }

        file.setUniforms(processedUniforms);
    }

    private static void preprocessVulkanShader(ShaderFile file, List<Parser.Declaration> declarationUniforms, List<Parser.Interface> interfaceUniforms) {
        var fileUniforms = processExistingInterfaces(interfaceUniforms);
        if (defaultVulkanTexture == null) defaultVulkanTexture = Resource.getTexture("default.png");

        for (var uniform : declarationUniforms) {
            if (uniform.type.value.contains("sampler")) {
                var layoutMaybe = uniform.modifiers.modifiers.stream().filter(i -> i instanceof Parser.Layout).map(i -> (Parser.Layout) i).findFirst();
                var setMaybe = layoutMaybe.flatMap(l -> l.expressions.stream().filter(e -> e instanceof Parser.BinaryOp)
                        .map(e -> (Parser.BinaryOp) e).filter(b -> ((Parser.Identifier) b.left).value.equals("set")).map(b -> ((Parser.IntegerLiteral) b.right).value).findFirst());
                var bindingMaybe = layoutMaybe.flatMap(l -> l.expressions.stream().filter(e -> e instanceof Parser.BinaryOp)
                        .map(e -> (Parser.BinaryOp) e).filter(b -> ((Parser.Identifier) b.left).value.equals("binding")).map(b -> ((Parser.IntegerLiteral) b.right).value).findFirst());
                if (setMaybe.isPresent()) {
                    setMappings.put(uniform.name.value, setMaybe.get());
                    fileUniforms.add(new Uniform(uniform.name.value, new UniformPosition(setMaybe.get(), bindingMaybe.get(), 0), COMBINED_TEXTURE_SAMPLER));
                    currentDescriptorBindingImageValue.put(new DescriptorPosition(setMaybe.get(), bindingMaybe.get()), defaultVulkanTexture);
                    setCounter = Math.max(setCounter, setMaybe.get() + 1);
                    continue;
                }

                if (!setMappings.containsKey(uniform.name.value))
                    setMappings.put(uniform.name.value, setCounter++);

                var newSet = setMappings.get(uniform.name.value);
                var setOp = new Parser.BinaryOp(new Parser.Identifier("set"), new Parser.IntegerLiteral(newSet), "=");
                var bindingOp = new Parser.BinaryOp(new Parser.Identifier("binding"), new Parser.IntegerLiteral(0), "=");

                //Assign uniform binding values
                uniform.modifiers.modifiers.stream()
                        .filter(m -> m instanceof Parser.Layout)
                        .map(m -> (Parser.Layout) m)
                        .findFirst().ifPresentOrElse(
                        l -> l.expressions.add(bindingOp),
                        () -> uniform.modifiers.modifiers.add(new Parser.Layout(List.of(setOp, bindingOp))));

                fileUniforms.add(new Uniform(uniform.name.value, new UniformPosition(newSet, 0, 0), COMBINED_TEXTURE_SAMPLER));
                currentDescriptorBindingImageValue.put(new DescriptorPosition(newSet, 0), defaultVulkanTexture);
            } else {
                var newName = uniform.name.value.replaceAll("\\[.*]", "") + "_buf";
                if (!setMappings.containsKey(newName))
                    setMappings.put(newName, setCounter++);

                var newSet = setMappings.get(newName);
                var loc = file.getTree().nodes.indexOf(uniform);
                file.getTree().nodes.remove(uniform);
                uniform.modifiers.modifiers.removeIf(m -> m.value.equals("uniform"));

                var newInterface = new Parser.Interface();
                newInterface.accessor = new Parser.Identifier("uniform");
                newInterface.name = new Parser.Identifier(newName);
                newInterface.declarations = new Parser.Body();
                newInterface.declarations.expressions.add(uniform);

                var setOp = new Parser.BinaryOp(new Parser.Identifier("set"), new Parser.IntegerLiteral(newSet), "=");
                var bindingOp = new Parser.BinaryOp(new Parser.Identifier("binding"), new Parser.IntegerLiteral(0), "=");

                newInterface.modifiers.modifiers.add(new Parser.Layout(List.of(new Parser.Identifier("std140"), setOp, bindingOp)));
                file.getTree().nodes.add(loc, newInterface);

                fileUniforms.add(new Uniform(uniform.name.value, new UniformPosition(newSet, 0, 0), DescriptorType.UNIFORM_BUFFER));

                var size = glslTypeSizes.get(uniform.type.value);

                var buffer = Allocator.alloc(size != null ? size : 0);
                currentDescriptorBindingValue.put(new DescriptorPosition(newSet, 0), buffer);
            }
        }
        file.setUniforms(fileUniforms);
        uniformDescriptorPositions.putAll(fileUniforms.stream().collect(Collectors.toMap(u -> u.name, u -> u.position)));
    }

    private static List<Uniform> processExistingInterfaces(List<Parser.Interface> interfaceUniforms) {
        var uniforms = new ArrayList<Uniform>();
        for (var interfacee : interfaceUniforms) {
            var layout = interfacee.modifiers.modifiers.stream().filter(i -> i instanceof Parser.Layout).map(i -> (Parser.Layout) i).findFirst().get();
            var set = layout.expressions.stream().filter(e -> e instanceof Parser.BinaryOp)
                    .map(e -> (Parser.BinaryOp) e).filter(b -> ((Parser.Identifier) b.left).value.equals("set")).map(b -> ((Parser.IntegerLiteral) b.right).value).findFirst()
                    .get();
            var binding = layout.expressions.stream().filter(e -> e instanceof Parser.BinaryOp)
                    .map(e -> (Parser.BinaryOp) e).filter(b -> ((Parser.Identifier) b.left).value.equals("binding")).map(b -> ((Parser.IntegerLiteral) b.right).value).findFirst()
                    .get();

            int sizeCounter = 0;
            var position = new DescriptorPosition(set, binding);
            for (var dec : interfacee.declarations.expressions) {
                var declaration = (Parser.Declaration) dec;

                uniforms.add(new Uniform(declaration.name.value, new UniformPosition(position, sizeCounter), DescriptorType.UNIFORM_BUFFER));

                var size = glslTypeSizes.get(declaration.type.value);
                if (size != null)
                    sizeCounter += size;

            }
            var buffer = Allocator.alloc(sizeCounter);
            currentDescriptorBindingValue.put(position, buffer);

            interfacee.modifiers.modifiers.removeIf(m -> m.value.equals("unwrap"));
        }
        return uniforms;
    }

    private static void generateVertexInputPositions(ShaderFile file) {
        if (file.getType() == ShaderFile.ShaderFileType.VERT || file.getType() == ShaderFile.ShaderFileType.UTIL) { //setup input attributes
            var topLevel = file.getTree().nodes;
            var inputs = topLevel.stream()
                    .filter(n -> n instanceof Parser.Declaration)
                    .map(n -> (Parser.Declaration) n)
                    .filter(n -> n.modifiers.modifiers.stream()
                            .map(i -> i.value)
                            .anyMatch(s -> s.equals("in")))
                    .collect(Collectors.toList());
            for (var in : inputs) {
                if (in.modifiers.modifiers.stream().anyMatch(m -> m instanceof Parser.Layout)) continue;
                if (!attributeLocations.containsKey(in.name.value)) {
                    attributeLocations.put(in.name.value, attributeCounter);
                    attributeCounter++;
                }
                in.modifiers.modifiers.add(0, new Parser.Layout(List.of(new Parser.BinaryOp(new Parser.Identifier("location"), new Parser.IntegerLiteral(attributeLocations.get(in.name.value)), "="))));
            }
        }
    }

    public static ShaderProgram createShader(String name, ShaderProgram.ShaderType type, String source, List<Uniform> uniforms) {
        return ShaderProgram.create(type, source, name, uniforms);
    }

    private ShaderController() {
    }

    public record DescriptorPosition(int set, int binding) {
    }

    public record UniformPosition(DescriptorPosition descriptor, int offset) {
        public UniformPosition(int set, int binding, int offset) {
            this(new DescriptorPosition(set, binding), offset);
        }
    }

    public record Uniform(String name, UniformPosition position, DescriptorType type) {
    }

    public enum DescriptorType {
        UNIFORM_BUFFER, COMBINED_TEXTURE_SAMPLER
    }

}
