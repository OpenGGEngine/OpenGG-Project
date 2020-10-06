package com.opengg.core.render.shader;

import com.opengg.core.math.*;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;

public sealed interface UniformContainer {
    ByteBuffer getBuffer();

    final record FloatContainer(float contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return Allocator.alloc(Float.BYTES).putFloat(contents);
        }
    }

    final record DoubleContainer(double contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return Allocator.alloc(Float.BYTES).putDouble(contents);
        }
    }

    final record IntContainer(int contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return Allocator.alloc(Float.BYTES).putInt(contents);
        }
    }

    final record UintContainer(int contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return Allocator.alloc(Float.BYTES).putInt(contents);
        }
    }

    final record Matrix4fContainer(Matrix4f contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Matrix3fContainer(Matrix3f contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Vector2fContainer(Vector2f contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Vector3fContainer(Vector3f contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Vector4fContainer(Vector4f contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Vector2iContainer(Vector2i contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record Vector3iContainer(Vector3i contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents.getByteBuffer();
        }
    }

    final record ByteBufferContainer(ByteBuffer contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            return contents;
        }
    }

    final record TextureContainer(Texture contents) implements UniformContainer{
        @Override
        public ByteBuffer getBuffer() {
            throw new UnsupportedOperationException();
        }
    }
}




