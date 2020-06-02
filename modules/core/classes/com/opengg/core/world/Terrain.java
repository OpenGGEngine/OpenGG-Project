/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.geom.Triangle;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.system.Allocator;
import com.opengg.core.world.generators.HeightsGenerator;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Warren
 */
public class Terrain {

    private static final int MAX_COLOR = 255 * 255 * 255;

    private Renderable renderable;
    private List<Triangle> mesh;
    
    private float[][] map;
    private int sizex, sizez;
    private float xsquarewidth, zsquarewidth;

    private float maxHeight = 10;

    private String source;

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
        GGConsole.log("Generating terrain from " + path);
        try {
            BufferedImage image = ImageIO.read(new FileInputStream(Resource.getAbsoluteFromLocal(path)));

            this.sizex = image.getWidth();
            this.sizez = image.getHeight();

            map = new float[this.sizex][this.sizez];

            for (int i = 0; i < this.sizex; i++) {
                for (int j = 0; j < this.sizez; j++) {
                    map[i][j] = generateHeight(i, j, image) ;        
                }
            }
        } catch (FileNotFoundException ex) {
            GGConsole.error(path +" not found.");
            map = new float[1][1];
        } catch (IOException ex) {
            GGConsole.error("IOException: " + path);
            map = new float[1][1];
        }
    }
    
    private void genProcedural(HeightsGenerator gen, int gx, int gz){
        this.sizex = gx;
        this.sizez = gz;

        map = new float[gx][gz];
         for (int i = 0; i < gx; i++) {
            for (int j = 0; j < gz; j++) {
                map[i][j] = generateHeight(i,j,gen);
            }    
        }
    }
    private float generateHeight(int x, int z, BufferedImage image) {
        if (x < 0 || x > image.getWidth() || z < 0 || z > image.getHeight()) {
            return 0;
        }
        return (image.getRGB(x, z) / (float) MAX_COLOR) * maxHeight;
    }
    
    private float generateHeight(int x,int z,HeightsGenerator generator){
        return generator.getHeight(x,z)*maxHeight;
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
            normal = new Vector3f(0,1,0);
        }
        return normal;
    }

    public List<Triangle> getMesh(){
        if(mesh != null) return mesh;

        List<Vector3f> points = new ArrayList<>( map.length * map[0].length);
        List<Integer> indices = new ArrayList<>(6 * (map.length * map[0].length));
        List<Triangle> triangle = new ArrayList<>((3 * (map.length * map[0].length)));
        
        for(int i = 0; i < map.length-1; i++){
            for(int i2 = 0; i2 < map[0].length-1; i2++){
                int topLeft = (i * map.length) + i2;
                int topRight = topLeft + 1;
                int bottomLeft = ((i + 1) * map.length) + i2;
                int bottomRight = bottomLeft + 1;
                
                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(topLeft);
                
                indices.add(bottomRight);
                indices.add(bottomLeft);
                indices.add(topRight); 
            }
        }
        
        for(int i = 0; i < map.length; i++){
            for(int i2 = 0; i2 < map[0].length; i2++){
                float x = (i * xsquarewidth);
                float y = map[i][i2];
                float z = (i2 * zsquarewidth);
                points.add(new Vector3f(x,y,z));
            }
        }
        
        for(int i = 0; i < indices.size()-4; i += 4){
            Vector3f v1 = points.get(indices.get(i));
            Vector3f v2 = points.get(indices.get(i+1));
            Vector3f v3 = points.get(indices.get(i+2));
            Vector3f v4 = points.get(indices.get(i+3));
            
            Triangle t1 = new Triangle(v1,v2,v3);
            Triangle t2 = new Triangle(v3,v4,v1);
            triangle.add(t1);
            triangle.add(t2);
        }
        mesh = triangle;
        return mesh;
    }
    
    public List<Triangle> getMesh(int sx, int sy, int width, int height){
        List<Vector3f> points = new ArrayList<>( width*height);
        List<Integer> indices = new ArrayList<>(6 * width * height);
        List<Triangle> triangle = new ArrayList<>(3 * width * height);
        
        for(int i = 0; i < width-1; i++){
            for(int i2 = 0; i2 < height-1; i2++){
                int topLeft = (i * width) + i2;
                int topRight = topLeft + 1;
                int bottomLeft = ((i + 1) * width) + i2;
                int bottomRight = bottomLeft + 1;
                
                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(topLeft);
                
                indices.add(bottomRight);
                indices.add(bottomLeft);
                indices.add(topRight); 
            }
        }
        
        for(int i = 0; i < width; i++){
            for(int i2 = 0; i2 < height; i2++){
                float x = ((i + sx) * xsquarewidth);
                float y = map[i+sx][i2+sy];
                float z = ((i2 + sy) * zsquarewidth);

                points.add(new Vector3f(x,y,z));
            }
        }
        
        for(int i = 0; i < indices.size()-4; i += 4){
            Vector3f v1 = points.get(indices.get(i));
            Vector3f v2 = points.get(indices.get(i+1));
            Vector3f v3 = points.get(indices.get(i+2));
            Vector3f v4 = points.get(indices.get(i+3));
            
            Triangle t1 = new Triangle(v1,v2,v3);
            Triangle t2 = new Triangle(v3,v4,v1);
            triangle.add(t1);
            triangle.add(t2);
        }
        return triangle;
    }
    
    public Renderable getRenderable(){
        if(renderable != null) return renderable;
        
        FloatBuffer bf = Allocator.allocFloat(8 * map.length * map[0].length);
        IntBuffer indices = Allocator.allocInt(6 * map.length * map[0].length);
        
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
                float x = (i * xsquarewidth);
                float y = map[i][i2];
                float z = (i2 * zsquarewidth);
                                
                float u = (float)i / (float) map.length;
                float v = (float)i2 / (float) map[0].length;
                
                float nx = 1;
                float ny = 1;
                float nz = 1;
                if(i > 0 && i < map.length - 1 && i2  > 0 && i2 < map[0].length - 1){
                    Vector3f normal = calculateNormal(i,i2);
                    nx = normal.x;
                    ny = normal.y;
                    nz = normal.z;
                }

                bf.put(x).put(y).put(z).put(nx).put(ny).put(nz).put(u).put(v);

            }
        }
        bf.flip();
        indices.flip();
        this.renderable = DrawnObject.create(indices,bf);
        return this.renderable;
    }
    
    public Vector3f getNormalAt(float x, float z){
        int gridx = (int) (x / xsquarewidth);
        int gridz = (int) (z / zsquarewidth);
        
        if(gridx >= map.length-1 || gridx <= 0 || gridz >= map[0].length-1 || gridz <= 0)
            return null;
        
        return calculateNormal(gridx,gridz);
    }
    
    public float getHeight(float x, float z){
        int gridx = (int) (x / xsquarewidth);
        int gridz = (int) (z / zsquarewidth);
        
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
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.x - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z))/det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z))/det;
        float l3 = 1f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
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

    public int getLength() {
        return sizex;
    }

    public int getWidth() {
        return sizez;
    }

    public float[][] getMap() {
        return map;
    }
    

    public ByteBuffer getHeightmapBuffer(){
        ByteBuffer texBuffer = Allocator.alloc(map.length * map[0].length * 4);
        for(int j = 0; j < map.length; j++){
            for(int i = 0; i < map[j].length; i++){
                byte val2 = (byte) (map[i][j] * 16);
                texBuffer.put(val2).put(val2).put(val2).put((byte)0xFF);
            }
        }
        texBuffer.flip();
        return texBuffer;
    }
    
    public Texture getHeightmap(){
        TextureData data = new TextureData(map.length, map[0].length, 4, getHeightmapBuffer(), "autogen");
        return Texture.get2DTexture(data);
    }
    
    public String getSource(){
        return source;
    }
}
