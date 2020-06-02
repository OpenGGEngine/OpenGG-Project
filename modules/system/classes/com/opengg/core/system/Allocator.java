/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.system;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;

import java.nio.*;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Javier
 */
public class Allocator {
    //private static Map<Buffer, BufferData> buffers = Collections.synchronizedMap(new WeakHashMap<>());
    
    public static final int FLOAT = 0, INT = 1, SHORT = 2, BYTE = 3, LONG = 4;
    public static AllocType currentAllocator = AllocType.NATIVE_HEAP;

    static {
        GGInfo.setMemoryAllocator(currentAllocator.name());
    }

    public static void update(){
        
        if(getStackFrameIndex() != 0){
            GGConsole.warn("Stack frame index is at " + getStackFrameIndex() + " after an OpenGG frame");
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
        return allocFloat(size, AllocType.NATIVE_STACK);
    }
    
    public static FloatBuffer allocFloat(int size, AllocType allocator){
        FloatBuffer buffer = null;
        switch(allocator){
            case JAVA:
                buffer = FloatBuffer.allocate(size);
                break;
            case DIRECT:
                buffer = BufferUtils.createFloatBuffer(size);
                break;
            case NATIVE_HEAP:
                buffer = MemoryUtil.memAllocFloat(size);
                break;
            case NATIVE_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.callocFloat(size);
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static IntBuffer allocInt(int size){
        return allocInt(size, currentAllocator);
    }
    
    public static IntBuffer stackAllocInt(int size){
        return allocInt(size, AllocType.NATIVE_STACK);
    }
    
    public static IntBuffer allocInt(int size, AllocType allocator){
        IntBuffer buffer = null;
        switch(allocator){
            case JAVA:
                buffer = IntBuffer.allocate(size);
                break;
            case DIRECT:
                buffer = BufferUtils.createIntBuffer(size);
                break;
            case NATIVE_HEAP:
                buffer = MemoryUtil.memAllocInt(size);
                break;
            case NATIVE_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.callocInt(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static ByteBuffer alloc(int size){
        return alloc(size, currentAllocator);
    }
    
    public static ByteBuffer stackAlloc(int size){
        return alloc(size, AllocType.NATIVE_STACK);
    }
    
    public static ByteBuffer alloc(int size, AllocType allocator){
        ByteBuffer buffer = null;
        switch(allocator){
            case JAVA:
                buffer = ByteBuffer.allocate(size);
                break;
            case DIRECT:
                buffer = BufferUtils.createByteBuffer(size);
                break;
            case NATIVE_HEAP:
                buffer = MemoryUtil.memAlloc(size);
                break;
            case NATIVE_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.calloc(size);
                
                break;
        }
        register(buffer, allocator);
        return buffer;
    }
    
    public static ShortBuffer allocShort(int size){
        return allocShort(size, currentAllocator);
    }
    
    public static ShortBuffer stackAllocShort(int size){
        return allocShort(size, AllocType.NATIVE_STACK);
    }

    public static ShortBuffer allocShort(int size, AllocType allocator){
        ShortBuffer buffer = null;
        switch(allocator){
            case JAVA:
                buffer = ShortBuffer.allocate(size);
                break;
            case DIRECT:
                buffer = BufferUtils.createShortBuffer(size);
                break;
            case NATIVE_HEAP:
                buffer = MemoryUtil.memAllocShort(size);
                break;
            case NATIVE_STACK:
                MemoryStack stack = MemoryStack.stackPush();
                buffer = stack.mallocShort(size);

                break;
        }
        register(buffer, allocator);
        return buffer;
    }

    public static LongBuffer allocLong(int size){
        return allocLong(size, currentAllocator);
    }

    public static LongBuffer stackAllocLong(int size){
        return allocLong(size, AllocType.NATIVE_STACK);
    }

    public static LongBuffer allocLong(int size, AllocType allocator){
        LongBuffer buffer = switch (allocator) {
            case JAVA -> LongBuffer.allocate(size);
            case DIRECT -> BufferUtils.createLongBuffer(size);
            case NATIVE_HEAP -> MemoryUtil.memAllocLong(size);
            case NATIVE_STACK -> {
                MemoryStack stack = MemoryStack.stackPush();
                yield stack.mallocLong(size);
            }
        };
        register(buffer, allocator);
        return buffer;
    }
    
    public static void popStack(){
        MemoryStack.stackPop();
    }
    
    public static void register(Buffer buffer, AllocType allocator){
        int type = -1;
        if(buffer instanceof FloatBuffer) type = FLOAT;
        if(buffer instanceof ByteBuffer) type = BYTE;
        if(buffer instanceof IntBuffer) type = INT;
        if(buffer instanceof ShortBuffer) type = SHORT;

        if(allocator == AllocType.NATIVE_HEAP){
            /*var list = StackWalker.getInstance().walk(s ->
                    s
                            .skip(2)
                            .limit(5)
                            .filter(s3 -> s3.getClassName().contains("opengg"))
                            .map(s2 -> s2.getClassName() + ":" + s2.getMethodName())
                            .collect(Collectors.toList()));
            System.out.println(list);*/
            long address = MemoryUtil.memAddress(buffer);
            NativeResourceManager.register(buffer, () -> MemoryUtil.nmemFree(address));
        }
        
        //buffers.put(buffer, data);
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

    private Allocator() {
    }

    public enum AllocType {
        NATIVE_HEAP, NATIVE_STACK, DIRECT, JAVA
    }
}

class BufferData{
        int type;
        int size;
        Allocator.AllocType allocator;

        public int getSize(){
            return type;
        }
        
        public int getType(){
            return type;
        }
        
        public Allocator.AllocType getAllocator(){
            return allocator;
        }
    }
