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
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;

/**
 *
 * @author Warren
 */
public class Terrain {

    String heightmap;
    private float xt, zt;
    
    private int size;
    
    private List<FloatBuffer> buffers = new ArrayList<>();
    public IntBuffer indices;
    public FloatBuffer elementals;
    
    private Texture texture;

    public Terrain(int gridx, int gridz, Texture tex) {
        this.texture = tex;
     

    }

    public void generateTerrain(InputStream heightmap) throws IOException {
        
        BufferedImage image = null;
        ImageProcessor s = new ImageProcessor();
        image = ImageIO.read(heightmap);       
        int VERTEX_COUNT = image.getHeight();
        print(VERTEX_COUNT);
        indices = IntBuffer.allocate(6*((VERTEX_COUNT)*(VERTEX_COUNT)));
        //indices = BufferUtils.createIntBuffer(6*((VERTEX_COUNT)*(VERTEX_COUNT))*2);
       
        
        size = 540;
        FloatBuffer ud = BufferUtils.createFloatBuffer((image.getWidth()*image.getHeight())*12);
               
        for (int i = 0; i < image.getWidth(); i+=1) {
            for (int j = 0; j < image.getHeight(); j+=1) {
                
                float x1 = (j/(float)(VERTEX_COUNT-1))*size;
                float y = getHeight(i, j, image) ; 
                float z1 = (i/(float)(VERTEX_COUNT-1))*size;
              
                  
                

                Vector3f normal = calculateNormal(j,i, image);

             
                
                float u = (i)/(float)(VERTEX_COUNT-1);
                float v = (j)/(float)(VERTEX_COUNT-1);
               
                ud.put(x1).put(y).put(z1).put(0).put(0).put(0).put(0).put(normal.x).put(normal.y).put(normal.z).put(u).put(v);  
            }
                
            }
            ud.flip();
             elementals = ud;
          
            for(int i =0;i<VERTEX_COUNT-1;i++){
                for(int i2 = 0;i2<VERTEX_COUNT-1;i2++){
                    int topLeft = (i*VERTEX_COUNT) + i2;
                    int topRight = topLeft+1;
                    int bottomLeft = ((i+1)*VERTEX_COUNT)+i2;
                    int bottomRight = bottomLeft +1;
                  
                    indices.put(topLeft);
                    indices.put(bottomLeft);
                    indices.put(topRight);
                    indices.put(topRight);
                    indices.put(bottomLeft);
                    indices.put(bottomRight);
                  
                    
                }
            }
            
        
        indices.flip();
        
    }
     public void generateTerrain() throws IOException {
        HeightsGenerator generator = new HeightsGenerator();
        
        int VERTEX_COUNT = 186;
        indices = IntBuffer.allocate(6*((VERTEX_COUNT)*(VERTEX_COUNT)));
        //indices = BufferUtils.createIntBuffer(6*((VERTEX_COUNT)*(VERTEX_COUNT))*2);
       
        
        size = 1000;
        FloatBuffer ud = BufferUtils.createFloatBuffer((VERTEX_COUNT*VERTEX_COUNT)*12);
               
        for (int i = 0; i < VERTEX_COUNT; i+=1) {
            for (int j = 0; j < VERTEX_COUNT; j+=1) {
                
                float x1 = (j/(float)(VERTEX_COUNT-1))*size;
                float y = getHeight(i, j, generator) ; 
                float z1 = (i/(float)(VERTEX_COUNT-1))*size;
              
                  
                

                Vector3f normal = calculateNormal(j,i, generator);

             
                
                float u = (j)/(float)(VERTEX_COUNT-1);
                
                float v = (i)/(float)(VERTEX_COUNT-1);
                
                ud.put(x1).put(y).put(z1).put(0).put(0).put(0).put(0).put(normal.x).put(normal.y).put(normal.z).put(u).put(v);  
            }
                
            }
            ud.flip();
             elementals = ud;
          
            for(int i =0;i<VERTEX_COUNT-1;i++){
                for(int i2 = 0;i2<VERTEX_COUNT-1;i2++){
                    int topLeft = (i*VERTEX_COUNT) + i2;
                    int topRight = topLeft+1;
                    int bottomLeft = ((i+1)*VERTEX_COUNT)+i2;
                    int bottomRight = bottomLeft +1;
                  
                    indices.put(topLeft);
                    indices.put(bottomLeft);
                    indices.put(topRight);
                    indices.put(topRight);
                    indices.put(bottomLeft);
                    indices.put(bottomRight);
                  
                    
                }
            }
            
        
        indices.flip();
    }
    private float getHeight(int x, int z, BufferedImage image) {
        float height = 0;
        if (x < 0 || x > image.getWidth() || z < 0 || z > image.getHeight()) {
            return 0;
        }
        try{
            height = image.getRGB(x, z);
        }catch(Exception e){
            if(x == image.getWidth()){
                x--;
            }
            if(z == image.getHeight()){
                z--;
            }
             height = image.getRGB(x, z);
        }
        return (float) (((height/100000)*(0.001*size)));
    }
    private float getHeight(int x,int z,HeightsGenerator generator){
        return generator.generateHeight(x,z)*6;
    }
    private Vector3f calculateNormal(int x, int z, HeightsGenerator generator) {
        float heightl = getHeight(x - 1, z, generator);
        float heightr = getHeight(x + 1, z, generator);
        float heightd = getHeight(x, z - 1, generator);
        float heightu = getHeight(x, z + 1, generator);
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        normal.normalize();
        return normal;
        //return new Vector3f(0,0.1f,0);
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
    public void removeBuffer(){
      elementals.clear();
      indices.clear();
    }
    
}