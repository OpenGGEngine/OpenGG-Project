/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public class GGByteOutputStream extends OutputStream{
    private ByteArrayOutputStream baos;
    
    public GGByteOutputStream(){
        this.baos = new ByteArrayOutputStream();
    }
    
    public GGByteOutputStream(ByteArrayOutputStream baos){
        this.baos = baos;
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
    
    public void write(boolean b) throws IOException{
        write(b ? 1 : 0);
    }
    
    public void write(byte[] b) throws IOException{
        for(byte by : b){
            write(by);
        }
    }
    
    public void write(byte b) throws IOException{
        
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
    
    public byte[] getArray(){
        return baos.toByteArray();
    }
}
