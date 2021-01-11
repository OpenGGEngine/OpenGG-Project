package com.opengg.core.render.shader;

import java.util.List;

public record VertexArrayBinding(int bindingIndex, int vertexSize, int divisor, List<VertexArrayAttribute> attributes) {
    public static record VertexArrayAttribute(String name, int size, Type type, int offset){
        public enum Type{
            FLOAT,
            FLOAT2,
            FLOAT3,
            FLOAT4,
            HALF_FLOAT,
            HALF_FLOAT2,
            HALF_FLOAT4,
            BYTE,
            UNSIGNED_BYTE,
            INT,
            INT2,
            INT3,
            INT4
        }
    }
}
