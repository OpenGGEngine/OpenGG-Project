/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.objects;

import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.system.Allocator;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author Javier
 */
public class ObjectCreator {
    public static Drawable createQuadPrism(Vector3f c1, Vector3f c2){
        Buffer[] b = createQuadPrismBuffers(c1,c2);
        return new DrawnObject((IntBuffer)b[1], (FloatBuffer)b[0]);
    }
    
    public static Drawable createInstancedQuadPrism(Vector3f c1, Vector3f c2){
        Buffer[] b = createQuadPrismBuffers(c1,c2);
        return new DrawnObject((IntBuffer)b[1], (FloatBuffer)b[0], Allocator.allocFloat(3));
    }
    
    public static Buffer[] createQuadPrismBuffers(Vector3f c1, Vector3f c2){
        return ObjectBuffers.genQuadPrism(c1,c2);
    }
    
    public static Drawable createSquare(Vector2f c1, Vector2f c2, float z){
        var b = createSquareBuffers(c1, c2, z, Allocator.AllocType.MEM_STACK);
        var obj =  new DrawnObject(b.y, b.x);
        Allocator.popStack();
        Allocator.popStack();
        return obj;
    }
    
    public static Tuple<FloatBuffer, IntBuffer> createSquareBuffers(Vector2f c1, Vector2f c2, float z){
        return createSquareBuffers(c1,c2,z, Allocator.AllocType.MEMORYUTIL);
    }

    public static Tuple<FloatBuffer, IntBuffer> createSquareBuffers(Vector2f c1, Vector2f c2, float z, Allocator.AllocType type){
        return ObjectBuffers.getSquare(c1, c2, z, 1, false, type);

    }
    
    public static Drawable createCube(float size){
        Buffer[] b = ObjectBuffers.genCube(size);
        return new DrawnObject((IntBuffer)b[1], (FloatBuffer)b[0]);
    }

    private ObjectCreator() {
    }
}
