package com.opengg.core.render.internal.vulkan.shader;

import com.opengg.core.math.util.Tuple;
import com.opengg.core.render.internal.vulkan.VulkanDescriptorSetLayout;
import com.opengg.core.render.shader.ShaderPipeline;
import com.opengg.core.render.shader.ShaderProgram;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;

import java.util.*;
import java.util.stream.Collectors;

public class VulkanShaderPipeline implements ShaderPipeline {
    final private VulkanShaderStage vert, frag, tesc, tese, geom;
    final private VulkanDescriptorSetLayout[] usedLayouts;

    public VulkanShaderPipeline(ShaderProgram vert, ShaderProgram frag){
        this(vert, null, null, null, frag);
    }

    public VulkanShaderPipeline(ShaderProgram vert, ShaderProgram tesc, ShaderProgram tese, ShaderProgram geom, ShaderProgram frag){
        this.vert = (VulkanShaderStage) vert;
        this.tesc = (VulkanShaderStage) tesc;
        this.tese = (VulkanShaderStage) tese;
        this.geom = (VulkanShaderStage) geom;
        this.frag = (VulkanShaderStage) frag;

        usedLayouts = mergeLayouts();
    }

    private VulkanDescriptorSetLayout[] mergeLayouts(){
        int highestSet = Arrays.stream(new VulkanShaderStage[]{vert, tesc, tese, geom, frag})
                .filter(Objects::nonNull)
                .flatMap(s -> s.getDescriptorSets().stream())
                .mapToInt(Tuple::x)
                .max().getAsInt();

        var shaders = Arrays.stream(new VulkanShaderStage[]{vert, tesc, tese, geom, frag})
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var layouts = new HashMap<Integer, List<VulkanDescriptorSetLayout.Descriptor>>();
        for(var shader : shaders){
            for(var set : shader.getDescriptorSets()){
                if(!layouts.containsKey(set.x())) layouts.put(set.x(), new ArrayList<>(set.y().bindingList()));
                else{
                    for(var descriptor : set.y().bindingList()){
                        layouts.get(set.x()).stream()
                                .filter(d -> d.binding() == descriptor.binding())
                                .findFirst().ifPresentOrElse(d2 ->
                                layouts.get(set.x()).set(layouts.get(set.x()).indexOf(d2),
                                        new VulkanDescriptorSetLayout.Descriptor(d2.binding(), d2.type(), VK10.VK_SHADER_STAGE_ALL))
                        , () -> layouts.get(set.x()).add(descriptor));
                    }
                }
            }
        }

        var finalLayouts = new VulkanDescriptorSetLayout[highestSet+1];
        for(int i = 0; i <= highestSet; i++){
            var existing = layouts.get(i);
            finalLayouts[i] = new VulkanDescriptorSetLayout(Objects.requireNonNullElseGet(existing, List::of));
        }
        return finalLayouts;
    }

    @Override
    public void delete() {

    }

    @Override
    public void use() {
    }

    public VulkanDescriptorSetLayout[] getUsedLayouts() {
        return usedLayouts;
    }

    @Override
    public ShaderProgram getShader(ShaderProgram.ShaderType type) {
        return switch (type) {
            case VERTEX -> vert;
            case TESS_CONTROL -> tesc;
            case TESS_EVAL -> tese;
            case GEOMETRY -> geom;
            case FRAGMENT -> frag;
            case UTIL -> null;
        };
    }

    @Override
    public List<ShaderProgram> getShaders() {
        return null;
    }

    public VkPipelineShaderStageCreateInfo.Buffer getPipelineCreateInfo(){
        var shaders = new ArrayList<VulkanShaderStage>();
        shaders.add(vert);
        if(tesc != null) shaders.add(tesc);
        if(tesc != null) shaders.add(tese);
        if(tesc != null) shaders.add(geom);
        shaders.add(frag);
        return VulkanShaderStage.combineForUse(shaders);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VulkanShaderPipeline that = (VulkanShaderPipeline) o;

        if (!vert.equals(that.vert)) return false;
        if (!frag.equals(that.frag)) return false;
        if (!Objects.equals(tesc, that.tesc)) return false;
        if (!Objects.equals(tese, that.tese)) return false;
        return Objects.equals(geom, that.geom);
    }

    @Override
    public int hashCode() {
        int result = vert.hashCode();
        result = 31 * result + frag.hashCode();
        result = 31 * result + (tesc != null ? tesc.hashCode() : 0);
        result = 31 * result + (tese != null ? tese.hashCode() : 0);
        result = 31 * result + (geom != null ? geom.hashCode() : 0);
        return result;
    }
}
