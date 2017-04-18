/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.engine.GGConsole;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Warren
 */
public class Terrain {
    Drawable d;
    public float[][] map;
    public float sizex, sizez;

    private Terrain(int gridx, int gridz) {
        sizex = gridx;
        sizez = gridz;
    }

    public static Terrain generate(String mappath){
        
        Terrain t = new Terrain(1, 1);
        t.generateTexture(mappath);
        return t;
    }
    
    public static Terrain generateProcedural(HeightsGenerator generator, int gridx, int gridz){
        Terrain t = new Terrain(1, 1);
        t.genProcedural(generator, gridx, gridz);
        return t;
    }
    
    private void generateTexture(String path){
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(new File(path)));
            int gx = image.getWidth();
            int gz = image.getHeight();
            map = new float[gx][gz];
            GGConsole.log("Generating terrain from " + path);
            
            for (int i = 0; i < image.getWidth(); i+=1) {
                for (int j = 0; j < image.getHeight(); j+=1) {
                    map[i][j] = getHeight(i, j, image) ;        
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Terrain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void genProcedural(HeightsGenerator gen, int gx, int gz){   
        map = new float[gx][gz];
         for (int i = 0; i < gx; i+=1) {
            for (int j = 0; j < gz; j+=1) {
                map[i][j] = getHeight(i,j,gen);
            }    
        }
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
        return (float) (((height/100000)*(0.001*10)));
    }
    
    private float getHeight(int x,int z,HeightsGenerator generator){
        return generator.getHeight(x,z)*6f;
    }

    
    private Vector3f calculateNormal(int x, int z) {
        float heightl = map[x - 1][z];
        float heightr = map[x + 1][z];
        float heightd = map[x][z - 1];
        float heightu = map[x][z + 1];
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        normal.normalize();
        return normal;
    }

    
    public Drawable getDrawable(){
        FloatBuffer bf = MemoryUtil.memAllocFloat(12 * map.length * map[0].length);
        IntBuffer indices = MemoryUtil.memAllocInt(6 * (map.length * map[0].length));
        
        for(int i = 0; i < map.length-1; i++){
            for(int i2 = 0; i2 < map[0].length-1; i2++){
                int topLeft = (i * map.length) + i2;
                int topRight = topLeft + 1;
                int bottomLeft = ((i + 1) * map.length) + i2;
                int bottomRight = bottomLeft + 1;

                indices.put(topLeft);
                indices.put(bottomLeft);
                indices.put(topRight);
                indices.put(topRight);
                indices.put(bottomLeft);
                indices.put(bottomRight);
            }
        }
        
        for(int i = 0; i < map.length; i++){
            for(int i2 = 0; i2 < map[0].length; i2++){
                float x = ((float) i / (float) map.length) * (float) sizex;
                float y = map[i][i2];
                float z = ((float) i2 / (float) map[0].length) * (float) sizez;
                
                float u = (float) i / (float) map.length;
                float v = (float) i2 / (float) map[0].length;
                
                float nx = 1;
                float ny = 1;
                float nz = 1;
                if(i > 0 && i < map.length - 1 && i2  > 0 && i2 < map[0].length - 1){
                    Vector3f normal = calculateNormal(i,i2);
                    nx = normal.x;
                    ny = normal.y;
                    nz = normal.z;
                }

                bf.put(x).put(y).put(z).put(1).put(1).put(1).put(1).put(nx).put(ny).put(nz).put(u).put(v);
            }
        }
        bf.flip();
        indices.flip();
        d = new DrawnObject(bf, indices);
        return d;
    }
}