package com.opengg.core.render;

import com.opengg.core.render.shader.VertexArrayFormat;

import java.util.Objects;

public record SceneRenderUnit(Renderable renderable, UnitProperties renderableUnitProperties) implements Comparable<SceneRenderUnit>{

    @Override
    public int compareTo(SceneRenderUnit o) {
        if (this.renderableUnitProperties.manualPriority == o.renderableUnitProperties.manualPriority) {
            if(this.renderableUnitProperties.transparency == o.renderableUnitProperties.transparency){
                if(this.renderableUnitProperties.format.hashCode() == o.renderableUnitProperties.hashCode()){
                    if(this.renderableUnitProperties.shaderPipeline.equals(o.renderableUnitProperties.shaderPipeline)){
                        return 0;
                    } else {
                        return this.renderableUnitProperties.shaderPipeline.hashCode() - o.renderableUnitProperties.shaderPipeline.hashCode();
                    }
                } else {
                    return this.renderableUnitProperties.format.hashCode() - o.renderableUnitProperties.format.hashCode();
                }
            } else {
                return this.renderableUnitProperties.transparency ? 1 : -1;
            }
        } else {
            return this.renderableUnitProperties.manualPriority - o.renderableUnitProperties.manualPriority;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SceneRenderUnit that = (SceneRenderUnit) o;
        return renderable.equals(that.renderable) && renderableUnitProperties.equals(that.renderableUnitProperties);
    }

    public static class UnitProperties{
        final boolean transparency;
        final VertexArrayFormat format;
        final String shaderPipeline;
        final int manualPriority;
        private int hashCode = 0;

        public UnitProperties(boolean transparency, VertexArrayFormat format, String shaderPipeline, int manualPriority) {
            this.transparency = transparency;
            this.format = format;
            this.shaderPipeline = shaderPipeline;
            this.manualPriority = manualPriority;
        }

        public VertexArrayFormat format(){
            return format;
        }

        public String shaderPipeline(){
            return shaderPipeline;
        }

        public boolean transparency(){
            return transparency;
        }

        public int manualPriority(){
            return manualPriority;
        }

        public UnitProperties(){
            this(false, RenderEngine.getDefaultFormat(), "object", 0);
        }

        public UnitProperties transparency(boolean transparency){
            return new UnitProperties(transparency, format, shaderPipeline, manualPriority);
        }

        public UnitProperties format(VertexArrayFormat format){
            return new UnitProperties(transparency, format, shaderPipeline, manualPriority);
        }

        public UnitProperties shaderPipeline(String shaderPipeline){
            return new UnitProperties(transparency, format, shaderPipeline, manualPriority);
        }

        public UnitProperties manualPriority(int manualPriority){
            return new UnitProperties(transparency, format, shaderPipeline, manualPriority);
        }

        @Override
        public int hashCode() {
            if(hashCode == 0){
                hashCode = Objects.hash(transparency, format, shaderPipeline, manualPriority);
            }
            return hashCode;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            UnitProperties that = (UnitProperties) o;
            return transparency == that.transparency && manualPriority == that.manualPriority && format.equals(that.format) && shaderPipeline.equals(that.shaderPipeline);
        }
    }
}
