/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model.modelloaderplus;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author warre
 */
public class ModelLoaderPlus {
    static ArrayList<MNode> totals = new ArrayList<>();
    public static MModel loadModel(File file) throws IOException {
        FileChannel fp = new FileInputStream(file).getChannel();
        ByteBuffer numint = MemoryUtil.memAlloc(4);
        fp.read(numint);

        numint.rewind();
        int total =  numint.getInt();
      //  System.out.println(total);
        MModel m = new MModel(Double.toString(Math.random()));
        for(int i=0;i< total;i++){
            ByteBuffer size = MemoryUtil.memAlloc(4);
            fp.read(size);
            size.rewind();
            int size1 =  size.getInt();

            ByteBuffer vbo = MemoryUtil.memAlloc(size1);
            while(fp.read(vbo) > 0){

            }
            vbo.rewind();
            ByteBuffer slize = MemoryUtil.memAlloc(4);
            fp.read(slize);
            slize.rewind();
            int size2 =  slize.getInt();
            ByteBuffer ibo = MemoryUtil.memAlloc(size2);
            while(fp.read(ibo) > 0){

            }
           // System.out.println(size1+","+size2);
            ibo.rewind();
            MMesh mesh = new MMesh(vbo.asFloatBuffer(),ibo.asIntBuffer());

            ByteBuffer slize1 = MemoryUtil.memAlloc(4);
            fp.read(slize1);
            slize1.rewind();
            int total2 = slize1.getInt();
          //  System.out.println("Bone 7: "+total2);
            if(total2 !=0){
                mesh.bones = new MBone[total2];
                for(int i5 = 0;i5<total2;i5++){
                    ByteBuffer slize5 = MemoryUtil.memAlloc(4);
                    fp.read(slize5);
                    slize5.rewind();
                    int sizebuffer = slize5.getInt();
                //    System.out.println("Bone: "+sizebuffer);
                    ByteBuffer newkid = MemoryUtil.memAlloc(sizebuffer);
                    while(fp.read(newkid) > 0){

                    }
                    newkid.rewind();
                    int name = newkid.getInt()/2;
                    String stringName = "";
                    for(int i9 = 0;i9< name;i9++){
                        stringName += newkid.getChar();
                    }
                    int id = newkid.getInt();
                    Matrix4f initTransform = new Matrix4f(newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat());
                    MBone bone = new MBone();
                    bone.name = stringName;
                    bone.id = id;
                    System.out.println(id +"," + bone.name);
                    bone.offsetMatrix = initTransform;
                    mesh.bones[i5] = bone;

                }



            }

            m.addMesh(mesh);
        }
        fp.close();


        return m;
    }
    public static MModel loadModel(File file, File file2) throws IOException {

        FileChannel fp = new FileInputStream(file).getChannel();
        ByteBuffer numint = MemoryUtil.memAlloc(4);
        fp.read(numint);

        numint.rewind();
        int total =  numint.getInt();
       // System.out.println(total);
        MModel m = new MModel(Double.toString(Math.random()));
        for(int i=0;i< total;i++){
            ByteBuffer size = MemoryUtil.memAlloc(4);
            fp.read(size);
            size.rewind();
            int size1 =  size.getInt();

            ByteBuffer vbo = MemoryUtil.memAlloc(size1);
            while(fp.read(vbo) > 0){

            }
            vbo.rewind();
            ByteBuffer slize = MemoryUtil.memAlloc(4);
            fp.read(slize);
            slize.rewind();
            int size2 =  slize.getInt();
            ByteBuffer ibo = MemoryUtil.memAlloc(size2);
            while(fp.read(ibo) > 0){

            }
           // System.out.println(size1+","+size2);
            ibo.rewind();
            MMesh mesh = new MMesh(vbo.asFloatBuffer(),ibo.asIntBuffer());
            System.out.println("two towers: " + vbo.get(18-4-4));
            System.out.println("two towers: " + vbo.get(18-4-3));
            System.out.println("two towers: " + vbo.get(18-4-2));
            System.out.println("two towers: " + vbo.get(18-4-1));
            ByteBuffer slize1 = MemoryUtil.memAlloc(4);
            fp.read(slize1);
            slize1.rewind();
            int total2 = slize1.getInt();
            //System.out.println("Bone 7: "+total2);
            if(total2 !=0){
                mesh.bones = new MBone[total2];
                for(int i5 = 0;i5<total2;i5++){
                    ByteBuffer slize5 = MemoryUtil.memAlloc(4);
                    fp.read(slize5);
                    slize5.rewind();
                    int sizebuffer = slize5.getInt();
                    //System.out.println("Bone: "+sizebuffer);
                    ByteBuffer newkid = MemoryUtil.memAlloc(sizebuffer);
                    while(fp.read(newkid) > 0){

                    }
                    newkid.rewind();
                    int name = newkid.getInt()/2;
                    String stringName = "";
                    for(int i9 = 0;i9< name;i9++){
                        stringName += newkid.getChar();
                    }
                    int id = newkid.getInt();
                    Matrix4f initTransform = new Matrix4f(newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat(),newkid.getFloat());
                    MBone bone = new MBone();
                    bone.name = stringName;
                    bone.id = id;
                    System.out.println( id);
                    System.out.println(bone.name+","+total2);
                 //   System.out.println(bone.name);
                    bone.offsetMatrix = initTransform;
                    mesh.bones[i5] = bone;


                }



            }

            m.addMesh(mesh);
        }
        fp.close();

        FileChannel fp2 = new FileInputStream(file2).getChannel();
        ByteBuffer economiccollapse = MemoryUtil.memAlloc(16);
        fp2.read(economiccollapse);
        economiccollapse.flip();
        m.duration = economiccollapse.getDouble();
        m.tickspeed = economiccollapse.getDouble();
        MNode parent = recurStore(fp2, new MNode());
        m.root = parent;
        for(MNode node: totals){
            m.mnodes.add(node);
        }
        totals.clear();
        fp2.close();
        return m;
    }
    public static MNode recurStore(FileChannel fp2,MNode parent) throws IOException{
        System.out.println("recursion");
        MNode thus = new MNode();
        ByteBuffer initialnumber = MemoryUtil.memAlloc(4);
        fp2.read(initialnumber);
        initialnumber.rewind();


      //  initialnumber.rewind();
        int strlen = initialnumber.asIntBuffer().get() ;//initialnumber.getInt();
        var charbuffer = MemoryUtil.memAlloc(strlen);
        //System.out.println("Big d: " + fp2.position() + " sdsd " + fp2.size());
        while(charbuffer.hasRemaining()) {
            fp2.read(charbuffer);
        }
        charbuffer.rewind();
        String name="";

        thus.name =  StandardCharsets.US_ASCII.decode(charbuffer).toString();

        ByteBuffer positionnumber = MemoryUtil.memAlloc(4);
        fp2.read(positionnumber);
        positionnumber.rewind();

        System.out.println(thus.name + " " + thus.name.length());
        int positionlen = positionnumber.getInt();

        System.out.println(positionlen);
        ByteBuffer positionbuffer = MemoryUtil.memAlloc(positionlen * ((Float.BYTES * 3) + Double.BYTES));
        while(positionbuffer.hasRemaining()) {

            fp2.read(positionbuffer);
        }
        positionbuffer.rewind();
        Tuple<Double, Vector3f>[] positionkeys = new Tuple[positionlen];
        for(int i=0;i< positionlen;i++){
            double key = positionbuffer.getDouble();
            Vector3f f = new Vector3f(positionbuffer.getFloat(),positionbuffer.getFloat(),positionbuffer.getFloat());
            positionkeys[i] = new Tuple(key,f);
            System.out.println(positionkeys[i].toString());
        }
        thus.positionkeys = positionkeys;

        ByteBuffer rotationnumber = MemoryUtil.memAlloc(4);
        fp2.read(rotationnumber);
        rotationnumber.rewind();
        int rotationlen = rotationnumber.getInt();//rotationnumber.getInt();
        ByteBuffer rotationbuffer = MemoryUtil.memAlloc(rotationlen * ((Float.BYTES * 4) + Double.BYTES));
        while(rotationbuffer.hasRemaining()) {
            fp2.read(rotationbuffer);
        }
        rotationbuffer.rewind();
        Tuple<Double, Quaternionf>[] rotationkeys = new Tuple[rotationlen];
        System.out.println(rotationlen);
        for(int i=0;i< rotationlen;i++){
            double key = rotationbuffer.getDouble();
            Quaternionf f = new Quaternionf(rotationbuffer.getFloat(),rotationbuffer.getFloat(),rotationbuffer.getFloat(),rotationbuffer.getFloat());
            rotationkeys[i] = new Tuple(key,f);
            System.out.println(rotationkeys[i].toString());
        }
        thus.rotationkeys = rotationkeys;

        ByteBuffer scalingnumber = MemoryUtil.memAlloc(4);
        fp2.read(scalingnumber);
        scalingnumber.rewind();
        int scalinglen = scalingnumber.getInt();//initialnumber.getInt();
        ByteBuffer scalingbuffer = MemoryUtil.memAlloc(scalinglen * ((Float.BYTES * 3) + Double.BYTES));
        while(scalingbuffer.hasRemaining()) {
            fp2.read(scalingbuffer);
        }
        scalingbuffer.rewind();
        System.out.println(scalinglen);
        Tuple<Double, Vector3f>[] scalingkeys = new Tuple[scalinglen];
        for(int i=0;i< scalinglen;i++){
            double key = scalingbuffer.getDouble();
            Vector3f f = new Vector3f(scalingbuffer.getFloat(),scalingbuffer.getFloat(),scalingbuffer.getFloat());
            scalingkeys[i] = new Tuple(key,f);
            System.out.println(scalingkeys[i].toString());
        }
        thus.scalingkeys = scalingkeys;

        ByteBuffer nexttrain = MemoryUtil.memAlloc(16*4);
        while(nexttrain.hasRemaining()) {
            fp2.read(nexttrain);
        }
        nexttrain.rewind();
        Matrix4f transform = new Matrix4f(nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat(),nexttrain.getFloat());
        thus.transform = transform;
        ByteBuffer childnumber = MemoryUtil.memAlloc(4);
        fp2.read(childnumber);
        childnumber.rewind();
        int childlen = childnumber.getInt();
        System.out.println("better: " + childlen);
        totals.add(thus);
        for(int c = 0;c< childlen;c++){
            thus.children.add(recurStore(fp2,thus));
        }
        return thus;
    }
}
