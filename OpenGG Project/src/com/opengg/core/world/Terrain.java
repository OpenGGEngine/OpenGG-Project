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

/**
 *
 * @author Warren
 */
public class Terrain {
    private static float SIZE =800;
    private static float MAX_HEIGHT =800;
    private static float MAX_PIXEL_COLOR =800;
   // private static int VERTEX_COUNT = 128;
    String heightmap;
    private float x,z;
    private OBJModel model =  generateTerrain(heightmap);
    private Texture texture;
    public Terrain(int gridx,int gridz,Texture tex,String heightmap){
        this.x = gridx *SIZE;
        this.z = gridz *SIZE;
        this.texture = tex;
        this.heightmap = heightmap;
        
    }
   
	private OBJModel generateTerrain(String heightmap){
            BufferedImage image = null;
            ImageProcessor s = new ImageProcessor();
            image = s.loadImage(heightmap);
            int VERTEX_COUNT = image.getHeight();
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		int vertexPointer = 0;
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				vertices[vertexPointer*3] = (float)j/((float)VERTEX_COUNT - 1) * SIZE;
				vertices[vertexPointer*3+1] = getHeight(j,i,image);
				vertices[vertexPointer*3+2] = (float)i/((float)VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(j,i,image);
                                normals[vertexPointer*3] = normal.x;
				normals[vertexPointer*3+1] = normal.y;
				normals[vertexPointer*3+2] = normal.z;
				textureCoords[vertexPointer*2] = (float)j/((float)VERTEX_COUNT - 1);
				textureCoords[vertexPointer*2+1] = (float)i/((float)VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return model;
	}
        private float getHeight(int x,int z,BufferedImage image){
            if(x<0|| x>image.getHeight()||z<0||z>image.getHeight()){
                return 0;
            }
           float height = image.getRGB(x, z);
           height +=MAX_PIXEL_COLOR/2f;
           height /=MAX_PIXEL_COLOR/2f;
           height *=MAX_PIXEL_COLOR;
           return height;
        }
        private Vector3f calculateNormal(int x,int z,BufferedImage image){
            float heightl= getHeight(x-1,z,image);
            float heightr= getHeight(x+1,z,image);
            float heightd= getHeight(x,z-1,image);
            float heightu= getHeight(x,z+1,image);
            Vector3f normal = new Vector3f(heightl-heightr,2f,heightd-heightu);
            normal.normalize();
            return normal;
            
        }
}
