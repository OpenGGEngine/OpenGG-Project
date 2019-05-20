package com.opengg.core.util;

import com.opengg.core.system.Allocator;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class FastGGInputStream extends GGInputStream{
    public FastGGInputStream(InputStream bais) {
        super(bais);
    }

    public FastGGInputStream(byte[] data) {
        super(data);
    }

    public FastGGInputStream(ByteBuffer buffer) {
        super(buffer);
    }

    public FloatBuffer readFloatBuffer() throws IOException{
        int len = readInt() * Float.BYTES;
        ByteBuffer fb = Allocator.alloc(len).put(readNBytes(len));
        fb.flip();
        return fb.asFloatBuffer();
    }

    public IntBuffer readIntBuffer() throws IOException{
        int len = readInt() *Integer.BYTES;
        ByteBuffer ib = Allocator.alloc(len).put(readNBytes(len));
        ib.flip();
        return ib.asIntBuffer();
    }

    public ByteBuffer readByteBuffer() throws IOException{
        int len = readInt();
        ByteBuffer bb = Allocator.alloc(len).put(readNBytes(len));
        return bb.flip();
    }

    public float readFloat() throws IOException{
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public int readInt() throws IOException {
        byte[] temp = this.readNBytes(Integer.BYTES);
        return ((temp[0] << 24) + (temp[1] << 16) + (temp[2] << 8) + (temp[3] << 0));
    }

    public long readLong() throws IOException{
        byte[] temp = this.readNBytes(8);
        return (((long)temp[0] << 56) +
                ((long)(temp[1] & 255) << 48) +
                ((long)(temp[2] & 255) << 40) +
                ((long)(temp[3] & 255) << 32) +
                ((long)(temp[4] & 255) << 24) +
                ((temp[5] & 255) << 16) +
                ((temp[6] & 255) <<  8) +
                ((temp[7] & 255) <<  0));
    }
    public double readDouble() throws IOException{
        return Double.longBitsToDouble(readLong());
    }

    public char readChar() throws IOException{
        byte[] temp = this.readNBytes(2);
        return (char)((temp[0] << 8) + (temp[1] << 0));
    }
    public short readShort() throws IOException {
        return (short) readChar();
    }

    public boolean readBoolean() throws IOException{
        return (in.read() !=0 );
    }

    public String readString() throws IOException{
        int len = readInt();
        byte[] temp = readNBytes(len);
        return new String(temp);
    }

}
