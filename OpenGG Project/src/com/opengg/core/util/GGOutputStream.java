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
import com.opengg.core.math.Vector4f;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public class GGOutputStream extends OutputStream{
    private OutputStream out;
    
    public GGOutputStream(){
        this.out = new ByteArrayOutputStream();
    }
    
    public GGOutputStream(OutputStream baos){
        this.out = baos;
    }
    
    public void write(Vector2f v) throws IOException{
        write(v.x);
        write(v.y);
    }
    
    public void write(Vector3f v) throws IOException{
        write(v.x);
        write(v.y);
        write(v.z);
    }
    
    public void write(Vector4f v) throws IOException{
        write(v.x);
        write(v.y);
        write(v.z);
        write(v.w);
    }
    
    public void write(Matrix4f m) throws IOException{
        write(m.getBuffer());
    }
    
    public void write(Quaternionf q) throws IOException{       
        writeNormalized(q);
    }
    
    public void write(long l) throws IOException{
        write(ByteBuffer.allocate(Long.BYTES).putLong(l).array());
    }
    
    @Override
    public void write(int i) throws IOException{
        write(ByteBuffer.allocate(Integer.BYTES).putInt(i).array());
    }
    
    public void write(float f) throws IOException{
        write(ByteBuffer.allocate(Float.BYTES).putFloat(f).array());
    }
    public void write(double f) throws IOException{
        write(ByteBuffer.allocate(Double.BYTES).putDouble(f).array());
    }
    
    public void write(boolean b) throws IOException{
        write(b ? 1 : 0);
    }
    
    @Override
    public void write(byte[] b) throws IOException{
        out.write(b);
    }
    
    public void write(byte b) throws IOException{
        out.write(b);
    }
    
    public void write(char c) throws IOException{
        write(ByteBuffer.allocate(Character.BYTES).putChar(c).array());
    }
    
    public void write(String s) throws IOException{
        write(s.length());
        for(char c : s.toCharArray()){
            write(c);
        }
    }
    
    public void write(FloatBuffer fb) throws IOException{
        fb.rewind();
        write(fb.limit());
        while(fb.hasRemaining())
            write(fb.get());
    }
    
    public void write(IntBuffer ib) throws IOException{
        ib.rewind();
        write(ib.limit());
        while(ib.hasRemaining())
            write(ib.get());
    }
    
    public void writeNormalized(Vector2f v) throws IOException{
        write(v.x);
        write(v.y);
    }
    
    public void writeNormalized(Vector3f v) throws IOException{
        write(v.x);
        write(v.y);
        write(v.z);
    }
    
    public void writeNormalized(Quaternionf q) throws IOException{
        write(q.x);
        write(q.y);
        write(q.z);
        write(q.w);
    }
    
    public OutputStream getStream(){
        return out;
    }
    
    @Override
    public void flush() throws IOException{
        out.flush();
    }
    
    @Override
    public void close() throws IOException{
        out.close();
    }
}
