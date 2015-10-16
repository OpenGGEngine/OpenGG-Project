/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.ImageProcessor;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.texture.Texture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
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
    
    private List<FloatBuffer> buffers = new ArrayList<>();
    
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
        for (int i = 0; i < image.getWidth(); i+=2) {
            for (int j = 0; j < image.getHeight(); j+=2) {
                
                float x1 = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y = getHeight(j, i, image) ;
                float z1 = (float) (i) / ((float) VERTEX_COUNT - 1) * SIZE;   
                           
                float y1 = getHeight(j+1, i, image) ;
                
                float x2 = (float) (j+1) / ((float) VERTEX_COUNT - 1) * SIZE;            
                float y2 = getHeight(j+1, i+1, image) ;
                float z2 = (float) (i+1) / ((float) VERTEX_COUNT - 1) * SIZE;
                          
                float y3 = getHeight(j, i+1, image) ;
                
                Vector3f normal = calculateNormal(i, j, image);

                Vector3f normal2 = calculateNormal(i++, j, image);

                Vector3f normal3 = calculateNormal(i, j++, image);

                Vector3f normal4 = calculateNormal(i++, j++, image);
                
                float u = (float) j / ((float) VERTEX_COUNT - 1);
                float v = (float) i / ((float) VERTEX_COUNT - 1);
                float u2 = (float) j++ / ((float) VERTEX_COUNT - 1);
                float v2 = (float) i++ / ((float) VERTEX_COUNT - 1);               
                
                buffers.add(ObjectBuffers.getSquareTerrain(x1, z1, x2, z2, y, y1, y2, y3, 1, v, u, v2, u2, normal, normal2, normal3, normal4));
            }
        }
        
        FloatBuffer elements = BufferUtils.createFloatBuffer(buffers.size()*100);
        for(FloatBuffer buffer:buffers){
            for(int i = 0; i < buffer.limit(); i++){
                elements.put(buffer.get(i));
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
        //return 0;
        return height;
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightl = getHeight(x - 1, z, image);
        float heightr = getHeight(x + 1, z, image);
        float heightd = getHeight(x, z - 1, image);
        float heightu = getHeight(x, z + 1, image);
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        normal.normalize();
        //return normal;
        return new Vector3f(0,0.1f,0);
    }
}
