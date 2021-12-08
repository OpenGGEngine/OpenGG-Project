package com.opengg.core.render;

import com.opengg.core.render.shader.VertexArrayFormat;

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

    public record UnitProperties(boolean transparency, VertexArrayFormat format, String shaderPipeline, int manualPriority){

        public UnitProperties(boolean transparency, VertexArrayFormat format, String shaderPipeline){
           this(transparency, format, shaderPipeline, 0); 
        }

        public UnitProperties(){
            this(false, RenderEngine.getDefaultFormat(), "object");
        }

        public UnitProperties transparency(boolean transparency){
            return new UnitProperties(transparency, format, shaderPipeline);
        }

        public UnitProperties format(VertexArrayFormat format){
            return new UnitProperties(transparency, format, shaderPipeline);
        }

        public UnitProperties shaderPipeline(String shaderPipeline){
            return new UnitProperties(transparency, format, shaderPipeline);
        }
    }
}
