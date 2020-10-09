package com.opengg.core.render.shader;

import java.util.List;

public record VertexArrayBinding(int bindingIndex, int vertexSize, int divisor, List<VertexArrayAttribute> attributes) {
    public static record VertexArrayAttribute(String name, int size, Type type, int offset){
        public enum Type{
            FLOAT,
            FLOAT2,
            FLOAT3,
            FLOAT4,
            BYTE,
            INT
        }
    }
}
