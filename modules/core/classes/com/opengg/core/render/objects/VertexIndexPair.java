package com.opengg.core.render.objects;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public record VertexIndexPair(FloatBuffer vertices, IntBuffer indices) {
}
