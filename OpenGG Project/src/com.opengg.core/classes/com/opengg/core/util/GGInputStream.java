/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.system.Allocator;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public class GGInputStream extends InputStream{
    private InputStream in;
    
    public GGInputStream(InputStream bais){
        this.in = bais;
    }
    
    public GGInputStream(ByteBuffer buffer){
        if(buffer.hasArray()){
            this.in = new ByteArrayInputStream(buffer.array());
        }else{
            byte[] array = new byte[buffer.limit()];
            for(int i = 0; i < array.length; i++){
                array[i] = buffer.get();
            }
            this.in = new ByteArrayInputStream(array);
        }
    }
    
    public Matrix4f readMatrix4f() throws IOException{
        readInt();
        float l00 = readFloat();
        float l01 = readFloat();
        float l02 = readFloat();
        float l03 = readFloat();
        float l10 = readFloat();
        float l11 = readFloat();
        float l12 = readFloat();
        float l13 = readFloat();
        float l20 = readFloat();
        float l21 = readFloat();
        float l22 = readFloat();
        float l23 = readFloat();
        float l30 = readFloat();
        float l31 = readFloat();
        float l32 = readFloat();
        float l33 = readFloat();

        return new Matrix4f(l00,l01,l02,l03,
                                    l10,l11,l12,l13,
                                    l20,l21,l22,l23,
                                    l30,l31,l32,l33);
    }
    
    public Vector3f readVector3f() throws IOException{
        float x = readFloat();
        float y = readFloat();
        float z = readFloat();

        return new Vector3f(x,y,z);
    }
    
    public Vector2f readVector2f() throws IOException{
        float x = readFloat();
        float y = readFloat();
        return new Vector2f(x, y);
    }
    
    public Quaternionf readQuaternionf() throws IOException{
        float x = readFloat();
        float y = readFloat();
        float z = readFloat();
        float w = readFloat();

        return new Quaternionf(w,x,y,z);
    }
    
    public int readInt() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Integer.BYTES).put(readByteArray(Integer.BYTES));
        b.flip();
        return b.getInt();
    }

    public long readLong() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Long.BYTES).put(readByteArray(Long.BYTES));
        b.flip();
        return b.getLong();
    }
    
    public float readFloat() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Float.BYTES).put(readByteArray(Float.BYTES));
        b.flip();
        return b.getFloat();
    }
    public double readDouble() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Double.BYTES).put(readByteArray(Double.BYTES));
        b.flip();
        return b.getDouble();
    }
    
    public boolean readBoolean() throws IOException{
        int b = readInt();
        return b == 1;
    }
    
    public byte[] readByteArray(int size) throws IOException{
        byte[] b = new byte[size];
        in.read(b);
        return b;
    }
    
    public byte readByte() throws IOException{
        return (byte) read();
    }
    
    public char readChar() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Character.BYTES).put(readByteArray(Character.BYTES));
        b.flip();
        return (char)b.getShort();
    }
    
    public String readString() throws IOException{
        StringBuilder s = new StringBuilder();
        int len = readInt();
        for(int i = 0; i < len; i++){
            s.append(readChar());
        }
        return s.toString();
    }
    
    public FloatBuffer readFloatBuffer() throws IOException{
        int len = readInt();
        FloatBuffer fb = Allocator.allocFloat(len);
        for(int i = 0; i < len; i++){
            fb.put(readFloat());
        }
        fb.flip();
        return fb;
    }
    
    public IntBuffer readIntBuffer() throws IOException{
        int len = readInt();
        IntBuffer ib = Allocator.allocInt(len);
        for(int i = 0; i < len; i++){
            ib.put(readInt());
        }
        ib.flip();
        return ib;
    }
    
    public ByteBuffer readByteBuffer() throws IOException{
        int len = readInt();
        ByteBuffer bb = Allocator.alloc(len);
        for(int i = 0; i < len; i++){
            bb.put(readByte());
        }
        bb.flip();
        return bb;
    }
    
    public Vector2f readNormalizedVector2f() throws IOException{
        return readVector2f();
    }
    
    public Vector3f readNormalizedVector3f() throws IOException{
        return readVector3f();
    }
    
    public Quaternionf readNormalizedQuaternionf(){
        return new Quaternionf();
    }

    @Override
    public int read() throws IOException {
        return in.read();
    }
}
