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
    public static VertexIndexPair get3DRectangle(Vector3f p1, Vector3f p2, Vector3f p3, Vector3f p4){
        FloatBuffer sq = Allocator.allocFloat(4*8);

        sq.put(p1.x).put(p1.y).put(p1.z).put(1f).put(1f).put(1f).put(0).put(0);
        sq.put(p2.x).put(p2.y).put(p2.z).put(1f).put(1f).put(1f).put(1).put(0);
        sq.put(p3.x).put(p3.y).put(p3.z).put(1f).put(1f).put(1f).put(1).put(1);
        sq.put(p4.x).put(p4.y).put(p4.z).put(1f).put(1f).put(1f).put(0).put(1);
        sq.flip();

        IntBuffer indices = Allocator.allocInt(6);
        indices.put(new int[]{2,1,0,
                0,3,2});
        indices.flip();
        return new VertexIndexPair(sq, indices);
    }

    public static VertexIndexPair getSquare(Vector2f v1, Vector2f v2, float z1 , float transparency, boolean flippedTex, Allocator.AllocType type){
        FloatBuffer sq = Allocator.allocFloat(4*8, type);
        sq.put(v1.x).put(v1.y).put(z1).put(1f).put(1f).put(1f).put(0).put(0);
        sq.put(v1.x).put(v2.y).put(z1).put(1f).put(1f).put(1f).put(0).put(1);
        sq.put(v2.x).put(v2.y).put(z1).put(1f).put(1f).put(1f).put(1).put(1);
        sq.put(v2.x).put(v1.y).put(z1).put(1f).put(1f).put(1f).put(1).put(0);
        sq.flip();
        
        IntBuffer indices = Allocator.allocInt(6, type);
        indices.put(new int[]{2,1,0,
            0,3,2});
        indices.flip();
        return new VertexIndexPair(sq, indices);
    }

    static FloatBuffer getSquare(float x1, float z1, float x2, float z2, float y1,float y2, float y3, float y4,  float transparency,boolean flippedTex){
        FloatBuffer sq = Allocator.allocFloat(6*8);
        int i, i2;
        if(flippedTex){
            i = 1;
            i2 = 0;
            
        }else{
            i = 0;
            i2 = 1;
        }
        
        sq.put(x1).put(y1).put(z1).put(0f).put(0f).put(1f).put(1).put(i);
        sq.put(x1).put(y2).put(z2).put(0f).put(0f).put(1f).put(1).put(i2);
        sq.put(x2).put(y3).put(z1).put(0f).put(0f).put(1f).put(0).put(i);
        sq.put(x2).put(y3).put(z1).put(0f).put(0f).put(1f).put(0).put(i);
        sq.put(x2).put(y4).put(z2).put(0f).put(0f).put(1f).put(0).put(i2);
        sq.put(x1).put(y2).put(z2).put(0f).put(0f).put(1f).put(1).put(i2);
        
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

    static Buffer[] genQuadPrism(Vector3f c1, Vector3f c2){
        FloatBuffer d = Allocator.allocFloat(24*8);
        IntBuffer d2 = Allocator.allocInt(6*6);

        d.put(c1.x).put(c1.y).put(c1.z).put(0).put(-1).put(0).put(0).put(1);
        d.put(c1.x).put(c1.y).put(c2.z).put(0).put(-1).put(0).put(0).put(0);
        d.put(c2.x).put(c1.y).put(c2.z).put(0).put(-1).put(0).put(1).put(0);
        d.put(c2.x).put(c1.y).put(c1.z).put(0).put(-1).put(0).put(1).put(1);
        d2.put(new int[]{3,2,1,3,1,0});

        d.put(c1.x).put(c2.y).put(c1.z).put(0).put(1).put(0).put(0).put(1);
        d.put(c1.x).put(c2.y).put(c2.z).put(0).put(1).put(0).put(0).put(0);
        d.put(c2.x).put(c2.y).put(c2.z).put(0).put(1).put(0).put(1).put(0);
        d.put(c2.x).put(c2.y).put(c1.z).put(0).put(1).put(0).put(1).put(1);
        d2.put(new int[]{4,5,7,5,6,7});

        d.put(c1.x).put(c1.y).put(c1.z).put(-1).put(0).put(0).put(0).put(1);
        d.put(c1.x).put(c1.y).put(c2.z).put(-1).put(0).put(0).put(0).put(0);
        d.put(c1.x).put(c2.y).put(c2.z).put(-1).put(0).put(0).put(1).put(0);
        d.put(c1.x).put(c2.y).put(c1.z).put(-1).put(0).put(0).put(1).put(1);
        d2.put(new int[]{8,9,11,9,10,11});

        d.put(c2.x).put(c1.y).put(c1.z).put(1).put(0).put(0).put(0).put(1);
        d.put(c2.x).put(c1.y).put(c2.z).put(1).put(0).put(0).put(0).put(0);
        d.put(c2.x).put(c2.y).put(c2.z).put(1).put(0).put(0).put(1).put(0);
        d.put(c2.x).put(c2.y).put(c1.z).put(1).put(0).put(0).put(1).put(1);
        d2.put(new int[]{15,14,13,15,13,12});

        d.put(c1.x).put(c1.y).put(c1.z).put(0).put(0).put(-1).put(0).put(1);
        d.put(c1.x).put(c2.y).put(c1.z).put(0).put(0).put(-1).put(0).put(0);
        d.put(c2.x).put(c2.y).put(c1.z).put(0).put(0).put(-1).put(1).put(0);
        d.put(c2.x).put(c1.y).put(c1.z).put(0).put(0).put(-1).put(1).put(1);
        d2.put(new int[]{16,17,19,17,18,19});

        d.put(c1.x).put(c1.y).put(c2.z).put(0).put(0).put(1).put(0).put(1);
        d.put(c1.x).put(c2.y).put(c2.z).put(0).put(0).put(1).put(0).put(0);
        d.put(c2.x).put(c2.y).put(c2.z).put(0).put(0).put(1).put(1).put(0);
        d.put(c2.x).put(c1.y).put(c2.z).put(0).put(0).put(1).put(1).put(1);
        d2.put(new int[]{23,22,21,23,21,20});

        d.flip();
        d2.flip();
        return new Buffer[]{d,d2};
    }

    static FloatBuffer createPointFloatBuffer(List<Vector3f> points){
        FloatBuffer f = Allocator.allocFloat(points.size()*8);
        for(var p : points){
            f.put(p.x).put(p.y).put(p.z).put(0).put(1).put(0).put(0).put(0);
        }
        f.flip();
        return f;
    }


    public static List<Triangle> getFromPointCloud(List<Vector3f> points){
        return null;
    }

    private ObjectBuffers() {
    }

}
