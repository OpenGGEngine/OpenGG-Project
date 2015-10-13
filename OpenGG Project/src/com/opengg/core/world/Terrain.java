/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world;

import com.opengg.core.Vector3f;
import com.opengg.core.io.ImageProcessor;
import com.opengg.core.objloader.parser.OBJModel;
import com.opengg.core.objloader.parser.OBJNormal;
import com.opengg.core.objloader.parser.OBJParser;
import com.opengg.core.texture.Texture;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.Random;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

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
        float transparency = 1;
        FloatBuffer elements = BufferUtils.createFloatBuffer(count*18);
       // float[] vertices = new float[count * 3];
        //float[] normals = new float[count * 3];
        //float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                float x1 = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
               
                float y1 = getHeight(j, i, image);
                float z1 = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
                Vector3f normal = calculateNormal(i, j, image);
                float xn = normal.x;
                float yn = normal.y;
                float zn = normal.z;
                float u = (float) j / ((float) VERTEX_COUNT - 1);
                float v = (float) i / ((float) VERTEX_COUNT - 1);
                Random random = new Random();
                float colorg = random.nextFloat() % 10;
                float colorr = random.nextFloat() % 10;
                float colorb = random.nextFloat() % 10;
                vertexPointer++;
                elements.put(x1).put(y1).put(z1).put(colorr).put(colorg).put(colorb).put(transparency).put(xn).put(yn).put(zn).put(u).put(v);

                
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
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
