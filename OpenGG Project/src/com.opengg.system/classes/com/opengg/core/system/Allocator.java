/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.system;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Allocator {
    //private static Map<Buffer, BufferData> buffers = Collections.synchronizedMap(new WeakHashMap<>());
    
    public static final int DEFAULT = 1, LWJGL_STACK = 2, LWJGL_DEFAULT = 3;
    public static final int FLOAT = 0, INT = 1, SHORT = 2, BYTE = 3, LONG = 4;
    public static int currentAllocator = LWJGL_DEFAULT;
    
    public static void update(){
        
        if(getStackFrameIndex() != 0){
            GGConsole.warn("Stack frame index is at " + getStackFrameIndex() + ", possible leak");
        }
        
        if(GGInfo.shouldAgressivelyManageMemory()){
            while(getStackFrameIndex() < 0){
                popStack();
            }
        }
    }
    
    public static FloatBuffer allocFloat(int size){
        return allocFloat(size, currentAllocator);
    }
    
    public static FloatBuffer stackAllocFloat(int size){
        return allocFloat(size, LWJGL_STACK);
    }
    
    public static FloatBuffer allocFloat(int size, int allocator){
        FloatBuffer buffer = null;
        switch(allocator){
            case DEFAULT:
                buffer = FloatBuffer.allocate(size);
                break;
            case LWJGL_DEFAULT:
                buffer = MemoryUtil.memAllocFloat(size);
                break;
            case LWJGL_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.mallocFloat(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static IntBuffer allocInt(int size){
        return allocInt(size, currentAllocator);
    }
    
    public static IntBuffer stackAllocInt(int size){
        return allocInt(size, LWJGL_STACK);
    }
    
    public static IntBuffer allocInt(int size, int allocator){
        IntBuffer buffer = null;
        switch(allocator){
            case DEFAULT:
                buffer = IntBuffer.allocate(DEFAULT);
                break;
            case LWJGL_DEFAULT:
                buffer = MemoryUtil.memAllocInt(size);
                break;
            case LWJGL_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.mallocInt(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static ByteBuffer alloc(int size){
        return alloc(size, currentAllocator);
    }
    
    public static ByteBuffer stackAlloc(int size){
        return alloc(size, LWJGL_STACK);
    }
    
    public static ByteBuffer alloc(int size, int allocator){
        ByteBuffer buffer = null;
        switch(allocator){
            case DEFAULT:
                buffer = ByteBuffer.allocate(DEFAULT);
                break;
            case LWJGL_DEFAULT:
                buffer = MemoryUtil.memAlloc(size);
                break;
            case LWJGL_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.malloc(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static ShortBuffer allocShort(int size){
        return allocShort(size, currentAllocator);
    }
    
    public static ShortBuffer stackAllocShort(int size){
        return allocShort(size, LWJGL_STACK);
    }
    
    public static ShortBuffer allocShort(int size, int allocator){
        ShortBuffer buffer = null;
        switch(allocator){
            case DEFAULT:
                buffer = ShortBuffer.allocate(DEFAULT);
                break;
            case LWJGL_DEFAULT:
                buffer = MemoryUtil.memAllocShort(size);
                break;
            case LWJGL_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.mallocShort(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static void popStack(){
        MemoryStack.stackPop();
        GGConsole.logVerbose("Stack popped by " + getSender());
    }
    
    public static void register(Buffer buffer, int allocator){
        int type = -1;
        if(buffer instanceof FloatBuffer) type = FLOAT;
        if(buffer instanceof ByteBuffer) type = BYTE;
        if(buffer instanceof IntBuffer) type = INT;
        if(buffer instanceof ShortBuffer) type = SHORT;
        
        int size = buffer.capacity();
        
        BufferData data = new BufferData();
        data.allocator = allocator;
        data.size = size; 
        data.type = type;
        
        //buffers.put(buffer, data);
        GGConsole.logVerbose("class" + " allocated " + buffer.getClass().getSimpleName() + " with size " + size + " using allocator " + allocator);
    }
    
   /* public static int getLiveBufferCount(){
        return buffers.size();
    }*/
    
    public static int getStackFrameIndex(){
        return MemoryStack.stackGet().getFrameIndex();
    }
    
    private static String getSender(){
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        StackTraceElement e = null;
        for(StackTraceElement element : stack){
            if(!(element.getClassName().contains("Thread") || element.getClassName().contains("Allocator"))){
                e = element;
                break;
            }
        }
        return (e.getClassName()).substring(e.getClassName().lastIndexOf('.')+1);
    }
}

class BufferData{
        int type;
        int size;
        int allocator;

        public int getSize(){
            return type;
        }
        
        public int getType(){
            return type;
        }
        
        public int getAllocator(){
            return allocator;
        }
    }
