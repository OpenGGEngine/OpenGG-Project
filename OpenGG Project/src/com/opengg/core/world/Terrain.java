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
import static com.opengg.core.util.GlobalUtil.print;
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
    private float xt, zt;
    
    private List<FloatBuffer> buffers = new ArrayList<>();
    
    private Texture texture;

    public Terrain(int gridx, int gridz, Texture tex) {
        this.xt = gridx * SIZE;
        this.zt = gridz * SIZE;
        this.texture = tex;
     

    }

    public FloatBuffer generateTerrain(InputStream heightmap) throws IOException {
        BufferedImage image = null;
        ImageProcessor s = new ImageProcessor();
        image = ImageIO.read(heightmap);       
        int VERTEX_COUNT = image.getHeight();
        int count = VERTEX_COUNT * VERTEX_COUNT;
        for (int w = 0; w < image.getWidth(); w++) {
            
            for (int p = 0; p < image.getHeight(); p++) {
               // System.out.println(w+","+p);
                int j = p;
                int i = w;
                float x1 = i;//(float) j / ((float) VERTEX_COUNT - 1) * SIZE;   
                //float x1 = (float) (i) / ((float) VERTEX_COUNT - 1) * SIZE; 
               float z1 = j;//(float) (i) / ((float) VERTEX_COUNT - 1) * SIZE;  
                //float z1 = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;   

                float x2 = (i+1);//(float) (j+1) / ((float) VERTEX_COUNT - 1) * SIZE;    
              // float x2 = (float) (i+1) / ((float) VERTEX_COUNT - 1) * SIZE; 
                float z2 = (j+1);//(float) (i+1) / ((float) VERTEX_COUNT - 1) * SIZE;

                float y = getHeight(i, j, image) ;    
               
                float y1 = getHeight(i+1, j, image) ;
                float y2 = getHeight(i+1, j+1, image) ;
                System.out.println(w+","+p +","+y);
                j = p;
                i = w;
                float y3 = getHeight(i, j+1, image) ;

                Vector3f normal = calculateNormal(i, j, image);

                Vector3f normal2 = calculateNormal(i++, j, image);

                Vector3f normal3 = calculateNormal(i, j++, image);

                Vector3f normal4 = calculateNormal(i++, j++, image);
                
                float u = i;//(float) j / ((float) VERTEX_COUNT - 1);
                float v = j;//(float) i / ((float) VERTEX_COUNT - 1);
                float u2 = i+1;//(float) j++ / ((float) VERTEX_COUNT - 1);
                float v2 = j+1;//(float) i++ / ((float) VERTEX_COUNT - 1);               
                
                buffers.add(ObjectBuffers.getSquareTerrain(x1, z1, x2, z2, y, y1, y2, y3, 1, u, v, u2, v2, normal, normal2, normal3, normal4));
            }
        }
        
        FloatBuffer elements = BufferUtils.createFloatBuffer(buffers.size()*80);
        for(FloatBuffer buffer:buffers){
            for(int k = 0; k < buffer.limit(); k++){
                elements.put(buffer.get(k));
            }
            
        }
        
        elements.flip();
        return elements;
    }

    private float getHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x > image.getWidth() || z < 0 || z > image.getHeight()) {
            return -100;
        }
        float height = 0;
        try{
            height = image.getRGB(x, z);
        }catch(Exception e){
            height = -100;
        }
        return (height/399990)+100;
       // return 0;
    }

    private Vector3f calculateNormal(int x, int z, BufferedImage image) {
        float heightl = getHeight(x - 1, z, image);
        float heightr = getHeight(x + 1, z, image);
        float heightd = getHeight(x, z - 1, image);
        float heightu = getHeight(x, z + 1, image);
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        normal.normalize();
        return normal;
        //return new Vector3f(0,0.1f,0);
    }
}
