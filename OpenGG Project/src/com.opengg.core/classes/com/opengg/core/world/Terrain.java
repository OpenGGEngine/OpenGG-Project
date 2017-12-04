/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
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
    
    float[][] map;
    float sizex, sizez;
    float xsquarewidth, zsquarewidth;
    String source;

    private Terrain(int gridx, int gridz) {
        sizex = gridx;
        sizez = gridz;
        
    }

    public static Terrain generate(TextureData data){
        return generate(data.source);
    }
    
    public static Terrain generate(String data){
        Terrain t = new Terrain(1, 1);
        t.generateTexture(data);
        t.xsquarewidth = 1/(float)t.map.length;
        t.zsquarewidth = 1/(float)t.map[0].length;
        t.source = data;
        t.normalize();
        return t;
    }
    
    public static Terrain generateProcedural(HeightsGenerator generator, int gridx, int gridz){
        Terrain t = new Terrain(1, 1);
        t.genProcedural(generator, gridx, gridz);
        t.xsquarewidth = 1/(float)t.map.length;
        t.zsquarewidth = 1/(float)t.map[0].length;
        t.source = "auto";
        t.normalize();
        return t;
    }
    
    private void generateTexture(String path){
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(Resource.getAbsoluteFromLocal(path)));

            int gx = image.getWidth();
            int gz = image.getHeight();
            map = new float[gx][gz];
            GGConsole.log("Generating terrain from " + path);
            
            for (int i = 0; i < image.getWidth(); i+=1) {
                for (int j = 0; j < image.getHeight(); j+=1) {
                    map[i][j] = generateHeight(i, j, image) ;        
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
                map[i][j] = generateHeight(i,j,gen);
            }    
        }
    }
    private float generateHeight(int x, int z, BufferedImage image) {
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
    
    private float generateHeight(int x,int z,HeightsGenerator generator){
        return generator.getHeight(x,z)*6f;
    }

    
    private Vector3f calculateNormal(int x, int z) {
        float heightl = map[x - 1][z];
        float heightr = map[x + 1][z];
        float heightd = map[x][z - 1];
        float heightu = map[x][z + 1];
        
        Vector3f normal = new Vector3f(heightl - heightr, 2f, heightd - heightu);
        try{
            normal = normal.normalize();
        }catch(Exception e){
            normal = new Vector3f(1,0,0);
        }
        return normal;
    }

    
    public Drawable getDrawable(){
        if(d != null)
            return d;
        
        FloatBuffer bf = MemoryUtil.memAllocFloat(12 * map.length * map[0].length);
        IntBuffer indices = MemoryUtil.memAllocInt(6 * (map.length * map[0].length));
        
        for(int i = 0; i < map.length-1; i++){
            for(int i2 = 0; i2 < map[0].length-1; i2++){
                int topLeft = (i * map.length) + i2;
                int topRight = topLeft + 1;
                int bottomLeft = ((i + 1) * map.length) + i2;
                int bottomRight = bottomLeft + 1;
                
                indices.put(topRight);
                indices.put(bottomLeft);
                indices.put(topLeft);
                
                indices.put(bottomRight);
                indices.put(bottomLeft);
                indices.put(topRight); 
            }
        }
        
        for(int i = 0; i < map.length; i++){
            for(int i2 = 0; i2 < map[0].length; i2++){
                float x = ((float) i * xsquarewidth);
                float y = map[i][i2];
                float z = ((float) i2 * zsquarewidth);
                                
                float u = (float) i / (float) map.length;
                float v = (float) i2 / (float) map[0].length;
                
                float nx = 1;
                float ny = 1;
                float nz = 1;
                if(i > 0 && i < map.length - 1 && i2  > 0 && i2 < map[0].length - 1){
                    Vector3f normal = calculateNormal(i,i2);
                    nx = normal.x();
                    ny = normal.y();
                    nz = normal.z();
                }

                bf.put(x).put(y).put(z).put(1).put(1).put(1).put(1).put(nx).put(ny).put(nz).put(u).put(v);
            }
        }
        bf.flip();
        indices.flip();
        d = new DrawnObject(bf, indices);
        return d;
    }
    
    public Vector3f getNormalAt(float x, float z){
        int gridx = (int) Math.floor(x / xsquarewidth);
        int gridz = (int) Math.floor(z / zsquarewidth);
        
        if(gridx >= map.length-1 || gridx <= 0 || gridz >= map[0].length-1 || gridz <= 0)
            return null;
        
        return calculateNormal(gridx,gridz);
    }
    
    public float getHeight(float x, float z){
        int gridx = (int) Math.floor(x / xsquarewidth);
        int gridz = (int) Math.floor(z / zsquarewidth);
        
        if(gridx >= map.length-1 || gridx <= 0 || gridz >= map[0].length-1 || gridz <= 0)
            return 12345;
        
        float xCoord = (x % xsquarewidth) / xsquarewidth;
        float zCoord = (z % zsquarewidth) / zsquarewidth;
        
        float answer = 0;
        if(xCoord <= (1-zCoord))
            answer = barycenterCompute(new Vector3f(0,map[gridx][gridz],0), new Vector3f(1, map[gridx+1][gridz],0),
                    new Vector3f(0,map[gridx][gridz+1],1), new Vector2f(xCoord,zCoord));
        else
            answer = barycenterCompute(new Vector3f(1,map[gridx+1][gridz],0), new Vector3f(1, map[gridx+1][gridz+1],1),
                    new Vector3f(0,map[gridx][gridz+1],1), new Vector2f(xCoord,zCoord));

        return answer;
    }
    
    public float barycenterCompute(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos){
        float det = (p2.z() - p3.z()) * (p1.x() - p3.x()) + (p3.x() - p2.x()) * (p1.x() - p3.z());
        float l1 = ((p2.z() - p3.z()) * (pos.x - p3.x()) + (p3.x() - p2.x()) * (pos.y - p3.z()))/det;
        float l2 = ((p3.z() - p1.z()) * (pos.x - p3.x()) + (p1.x() - p3.x()) * (pos.y - p3.z()))/det;
        float l3 = 1f - l1 - l2;
        return l1 * p1.y() + l2 * p2.y() + l3 * p3.y();
    }
    
    private void normalize(){
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        
        for(float[] row : map){
            for(float val : row){
                if(val > max)
                    max = val;
                if(val < min)
                    min = val;
            }
        }
        
        for(float [] row : map){
            for(float val : row){
                val -= min;
                val /= max - min;
            }
        }
    }
    
    public ByteBuffer getHeightmapBuffer(){
        ByteBuffer texBuffer = MemoryUtil.memAlloc(map.length * map[0].length * 4);
        for(int j = 0; j < map.length; j++){
            for(int i = 0; i < map.length; i++){
                byte val2 = (byte) (map[i][j] * 16);
                texBuffer.put(val2).put(val2).put(val2).put((byte)0xFF);
            }
        }
        texBuffer.flip();
        return texBuffer;
    }
    
    public Texture getHeightmap(){
        TextureData data = new TextureData(map.length, map[0].length, getHeightmapBuffer(), "autogen");
        return Texture.get2DTexture(data);
    }
    
    public String getSource(){
        return source;
    }
}
