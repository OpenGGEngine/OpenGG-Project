/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.objects;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.math.geom.Triangle;
import com.opengg.core.system.Allocator;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;

/**
 *
 * @author Javier
 */
public class ObjectBuffers {
    public static Buffer[] getSquare(Vector2f v1, Vector2f v2, float z1 ,float transparency, boolean flippedTex){
        FloatBuffer sq = Allocator.allocFloat(4*12);

        
        sq.put(v1.x).put(v1.y).put(z1).put(1).put(0).put(0).put(transparency).put(1f).put(0f).put(0f).put(0).put(0);
        sq.put(v1.x).put(v2.y).put(z1).put(0).put(1).put(0).put(transparency).put(1f).put(0f).put(0f).put(0).put(1);
        sq.put(v2.x).put(v2.y).put(z1).put(0).put(0).put(1).put(transparency).put(1f).put(0f).put(0f).put(1).put(1);   
        sq.put(v2.x).put(v1.y).put(z1).put(0).put(0).put(1).put(transparency).put(1f).put(0f).put(0f).put(1).put(0);           
        sq.flip();
        
        IntBuffer indices = Allocator.allocInt(6);
        indices.put(new int[]{0,1,2,
            2,3,0});
        indices.flip();
        return new Buffer[]{sq, indices};
    }

    static FloatBuffer getSquare(float x1, float z1, float x2, float z2, float y1,float y2, float y3, float y4,  float transparency,boolean flippedTex){
        FloatBuffer sq = Allocator.allocFloat(6*12);
        int i, i2;
        if(flippedTex){
            i = 1;
            i2 = 0;
            
        }else{
            i = 0;
            i2 = 1;
        }
        
        sq.put(x1).put(y1).put(z1).put(1).put(0).put(0).put(transparency).put(0f).put(0f).put(1f).put(1).put(i);
        sq.put(x1).put(y2).put(z2).put(0).put(1).put(0).put(transparency).put(0f).put(0f).put(1f).put(1).put(i2);
        sq.put(x2).put(y3).put(z1).put(0).put(0).put(1).put(transparency).put(0f).put(0f).put(1f).put(0).put(i);
        sq.put(x2).put(y3).put(z1).put(0).put(1).put(0).put(transparency).put(0f).put(0f).put(1f).put(0).put(i);
        sq.put(x2).put(y4).put(z2).put(0).put(0).put(1).put(transparency).put(0f).put(0f).put(1f).put(0).put(i2);
        sq.put(x1).put(y2).put(z2).put(1).put(0).put(0).put(transparency).put(0f).put(0f).put(1f).put(1).put(i2);
        
        sq.flip();
        return sq;
    }

    static FloatBuffer createDefaultBufferData(int size){
        FloatBuffer f = Allocator.allocFloat(size);
        for(int i = 0; i < size; i++){
            f.put(0);
        }
        f.flip();
        return f;
    }
    
    static Buffer[] genCube(float size){
        FloatBuffer sq = Allocator.allocFloat(8*12);
        
        sq.put(new float[]{-size,-size,-size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{size,-size,-size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{size,size,-size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{-size,size,-size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{-size,-size,size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{size,-size,size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{size,size,size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.put(new float[]{-size,size,size});
        sq.put(new float[]{1,1,1,1,1,1,1,1,1});
        
        sq.flip();
        
        IntBuffer ib = Allocator.allocInt(6 * 6);
        
        ib.put(new int[]{
                1,2,0,
                2,3,0,
                6,2,1,
                1,5,6,
                6,5,4,
                4,7,6,
                6,3,2,
                7,3,6,
                3,7,0,
                7,4,0,
                5,1,0,
                4,5,0
               });
        ib.flip();
        
        return new Buffer[]{sq,ib};
    }
    static Buffer[] genQuadPrism(Vector3f c1, Vector3f c2){
        FloatBuffer d = Allocator.allocFloat(8*12);
        d.put(c1.x).put(c1.y).put(c1.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);
        d.put(c1.x).put(c1.y).put(c2.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(0).put(1);
        d.put(c1.x).put(c2.y).put(c2.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);
        d.put(c1.x).put(c2.y).put(c1.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(0);
        
        d.put(c2.x).put(c1.y).put(c1.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1);
        d.put(c2.x).put(c1.y).put(c2.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(0).put(1);
        d.put(c2.x).put(c2.y).put(c2.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(0);
        d.put(c2.x).put(c2.y).put(c1.z).put(1).put(1).put(1).put(1).put(1).put(1).put(1).put(0).put(0);
        d.flip();
        
        IntBuffer d2 = Allocator.allocInt(6*4);
        d2.put(new int[]{0,1,3,1,2,3});
        d2.put(new int[]{4,6,7,5,6,7});
        
        d2.put(0).put(1).put(4).put(1).put(5).put(4);
        d2.put(2).put(3).put(6).put(3).put(7).put(6);
        
        
        d2.flip();
        return new Buffer[]{d,d2};
    }

    public static List<Triangle> getFromPointCloud(List<Vector3f> points){
        return null;
    }

    private ObjectBuffers() {
    }
}
