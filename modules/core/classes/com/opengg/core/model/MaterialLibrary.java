/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.util.HashMap;

/**
 *
 * @author Warren
 */
public class MaterialLibrary {

    public HashMap<String, Material> mats = new HashMap<>();

    public MaterialLibrary(){
        
    }
    public MaterialLibrary(String file) throws IOException {
        FileInputStream in = new FileInputStream(file);
       
        
        ScatteringByteChannel scatter = in.getChannel();
         ByteBuffer headerlength = ByteBuffer.allocate(4);
      
        scatter.read(headerlength);
        headerlength.rewind();
        int headlen = headerlength.getInt();
        ByteBuffer header = ByteBuffer.allocate(headlen *4);
        
        scatter.read(header);
        header.flip();
        header.rewind();
        
        ByteBuffer[] mats2 = new ByteBuffer[headlen];
        
        int i=0;
        while(header.hasRemaining()){
            int bufflen = header.getInt();
            mats2[i] = ByteBuffer.allocate(bufflen);
            scatter.read(mats2[i]);
            i++;
        }
        //scatter.read(mats2);
        
        for(ByteBuffer b:mats2){
            b.rewind();
            Material mat = new Material(b);
            mats.put(mat.name, mat);
        }
        
    }

    public void toFile(String file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);

        GatheringByteChannel gather = out.getChannel();

        ByteBuffer header = ByteBuffer.allocate((mats.size() + 1) * 4);
        System.out.println("Mats: " +mats.size());
        header.putInt(mats.size());
        ByteBuffer[] main = new ByteBuffer[mats.size() + 1];

        int i2 = 1;
        for (String i : mats.keySet()) {
            Material mat = mats.get(i);
            main[i2] = mat.toBuffer();
      
            header.putInt(mat.getCap());
            i2++;
        }
        header.flip();
        main[0] = header;
        for (ByteBuffer byteBuffer : main) {
            gather.write(byteBuffer);
        }
        //gather.write(main);
    }
}
