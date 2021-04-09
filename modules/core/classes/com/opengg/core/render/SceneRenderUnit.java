package com.opengg.core.render;

import com.opengg.core.render.shader.VertexArrayFormat;

public record SceneRenderUnit(Renderable renderable, UnitProperties renderableUnitProperties) implements Comparable<SceneRenderUnit>{

    @Override
    public int compareTo(SceneRenderUnit o) {
        if(o.renderableUnitProperties.transparency == this.renderableUnitProperties.transparency){
            if(this.renderableUnitProperties.format.hashCode() - o.renderableUnitProperties.hashCode() != 0){
                if(this.renderableUnitProperties.shaderPipeline.equals(o.renderableUnitProperties.shaderPipeline)){
                    return 0;
                }else{
                    return this.renderableUnitProperties.shaderPipeline.hashCode()-o.renderableUnitProperties.shaderPipeline.hashCode();
                }
            }else{
                if(this.renderableUnitProperties.shaderPipeline.equals(o.renderableUnitProperties.shaderPipeline)){
                    return 0;
                }else{
                    return this.renderableUnitProperties.format.hashCode()-o.renderableUnitProperties.format.hashCode();
                }
            }
        }else {
            return this.renderableUnitProperties.transparency ? 1 : 0;
        }
    }

    public record UnitProperties(boolean transparency, VertexArrayFormat format, String shaderPipeline){

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
