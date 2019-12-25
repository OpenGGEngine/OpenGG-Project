package com.opengg.core.render.shader;

import java.util.List;
import java.util.Objects;

public class VertexArrayBinding {
    private int bindingIndex;
    private int vertexSize;
    private int divisor;
    private List<VertexArrayAttribute> attributes;

    public VertexArrayBinding(int bindingIndex, int vertexSize, int divisor, List<VertexArrayAttribute> attributes) {
        this.bindingIndex = bindingIndex;
        this.vertexSize = vertexSize;
        this.attributes = attributes;
        this.divisor = divisor;
    }

    public int getBindingIndex() {
        return bindingIndex;
    }

    public int getVertexSize() {
        return vertexSize;
    }

    public int getDivisor() {
        return divisor;
    }

    public List<VertexArrayAttribute> getAttributes() {
        return attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VertexArrayBinding that = (VertexArrayBinding) o;

        if (bindingIndex != that.bindingIndex) return false;
        if (vertexSize != that.vertexSize) return false;
        if (divisor != that.divisor) return false;
        return Objects.equals(attributes, that.attributes);

    }

    @Override
    public int hashCode() {
        int result = bindingIndex;
        result = 31 * result + vertexSize;
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "VertexArrayBinding{" +
                "bindingIndex=" + bindingIndex +
                ", vertexSize=" + vertexSize +
                ", divisor=" + divisor +
                ", attributes=" + attributes +
                '}';
    }
}
