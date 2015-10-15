/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.ImageProcessor;
import com.opengg.core.render.texture.Texture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Warren
 */
public class Terrain {

    private static float SIZE = 800;
    private static float MAX_HEIGHT = 800;
    private static float MAX_PIXEL_COLOR = 800;
    // private static int VERTEX_COUNT = 128;
    String heightmap;
    private float x, z;

    private Texture texture;

    public Terrain(int gridx, int gridz, Texture tex) {
        this.x = gridx * SIZE;
        this.z = gridz * SIZE;
        this.texture = tex;
     

    }

    public FloatBuffer generateTerrain(InputStream heightmap) throws IOException {
        BufferedImage image = null;
        ImageProcessor s = new ImageProcessor();
        image = ImageIO.read(heightmap);       
        int VERTEX_COUNT = image.getHeight();
        int count = VERTEX_COUNT * VERTEX_COUNT;
        FloatBuffer elements = BufferUtils.createFloatBuffer(count*18*3);
        
        for (int i = 0; i < image.getWidth(); i=i+4) {
            for (int j = 0; j < image.getHeight(); j=j+4) {
                float x = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y = getHeight(j, i, image) ;
                float z = (float) (i+1) / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(i, j, image);
                float xn = normal.x;
                float yn = normal.y;
                float zn = normal.z;
                float x1 = (float) (j+1) / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y1 = getHeight(j+1, i+1, image) ;
                float z1 = (float) (i+1) / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal2 = calculateNormal(i, j, image);
                float xn1 = normal2.x;
                float yn1 = normal2.y;
                float zn1 = normal2.z;
                float x2 = (float) (j+2) / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y2 = getHeight(j+2, i+2, image) ;
                float z2 = (float) (i+2) / ((float) VERTEX_COUNT - 1) * SIZE;
                 float x3 = (float) (j+3) / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y3 = getHeight(j+3, i+3, image) ;
                float z3 = (float) (i+3) / ((float) VERTEX_COUNT - 1) * SIZE;
                
                float u = (float) j / ((float) VERTEX_COUNT - 1);
                float v = (float) i / ((float) VERTEX_COUNT - 1);
               
                 elements.put(x).put(0).put(z).put(255).put(255).put(255).put(1).put(xn).put(yn).put(zn).put(u).put(v);
                 elements.put(x1).put(0).put(z1).put(0).put(0).put(255).put(1).put(xn1).put(yn1).put(zn1).put(u).put(v);
                 elements.put(x3).put(0).put(z3).put(255).put(255).put(255).put(1).put(xn).put(yn).put(zn).put(u).put(v);
                 elements.put(x3).put(0).put(z3).put(255).put(255).put(255).put(1).put(xn).put(yn).put(zn).put(u).put(v);
                 elements.put(x1).put(0).put(z1).put(0).put(0).put(255).put(1).put(xn1).put(yn1).put(zn1).put(u).put(v);
                 elements.put(x2).put(0).put(z2).put(255).put(255).put(255).put(1).put(xn).put(yn).put(zn).put(u).put(v);
            }
        }
       
        elements.flip();
        return elements;
    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x > image.getWidth() || z < 0 || z > image.getHeight()) {
            return 0;
        }
        float height = 0;
       try{
         height = image.getRGB(x, z);
       }catch(Exception e){
           height = 0;
       }
        height += MAX_PIXEL_COLOR / 2f;
        height /= MAX_PIXEL_COLOR / 2f;
        height *= MAX_PIXEL_COLOR;
        return height;
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightl = getHeight(x - 1, z, image);
        float heightr = getHeight(x + 1, z, image);
        float heightd = getHeight(x, z - 1, image);
        float heightu = getHeight(x, z + 1, image);
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        normal.normalize();
        return normal;

    }
}
