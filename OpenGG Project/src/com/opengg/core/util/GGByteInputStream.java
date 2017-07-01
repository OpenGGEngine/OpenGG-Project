/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.util;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public class GGByteInputStream extends InputStream{
    private ByteArrayInputStream bais;
    
    public GGByteInputStream(ByteArrayInputStream bais){
        this.bais = bais;
    }
    
    public GGByteInputStream(ByteBuffer buffer){
        if(buffer.hasArray()){
            this.bais = new ByteArrayInputStream(buffer.array());
        }else{
            byte[] array = new byte[buffer.limit()];
            for(int i = 0; i < array.length; i++){
                array[i] = buffer.get();
            }
            this.bais = new ByteArrayInputStream(array);
        }
    }
    
    public Vector3f readVector3f() throws IOException{
        Vector3f v = new Vector3f();
        v.x = readFloat();
        v.y = readFloat();
        v.z = readFloat();
        return v;
    }
    
    public Vector2f readVector2f() throws IOException{
        Vector2f v = new Vector2f();
        v.x = readFloat();
        v.y = readFloat();
        return v;
    }
    
    public Quaternionf readQuaternionf() throws IOException{
        Quaternionf f = new Quaternionf();
        f.x = readFloat();
        f.y = readFloat();
        f.z = readFloat();
        f.w = readFloat();
        return f;
    }
    
    public int readInt() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Integer.BYTES).put(readByteArray(Integer.BYTES));
        b.flip();
        return b.getInt();
    }
    
    public float readFloat() throws IOException{
        ByteBuffer b = ByteBuffer.allocate(Float.BYTES).put(readByteArray(Float.BYTES));
        b.flip();
        return b.getFloat();
    }
    
    public boolean readBoolean() throws IOException{
        byte b = readByte();
        return b == 1;
    }
    
    public byte[] readByteArray(int size) throws IOException{
        byte[] b = new byte[size];
        for(int i = 0; i < size; i++){
            b[i] = readByte();
        }
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
        String s = "";
        int len = readInt();
        for(int i = 0; i < len; i++){
            s += readChar();
        }
        return s;
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
        return bais.read();
    }
}
