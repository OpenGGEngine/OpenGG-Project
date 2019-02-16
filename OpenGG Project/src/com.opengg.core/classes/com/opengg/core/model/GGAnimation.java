package com.opengg.core.model;

import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Tuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

public class GGAnimation {
    public String name;
    public double duration,ticksPerSec;

    public HashMap<String, AnimNode> animdata = new HashMap<>();

    public GGAnimation(String name, double duration, double ticksPerSec) {
        this.name = name;
        this.duration = duration;
        this.ticksPerSec = ticksPerSec;
    }

    public GGAnimation(ByteBuffer b){
        this.name = MLoaderUtils.readString(b);
        this.duration = b.getDouble();
        this.ticksPerSec = b.getDouble();
        int nodeSizes = b.getInt();
        for(int i=0;i<nodeSizes;i++){
            AnimNode node = new AnimNode(b); animdata.put(node.name,node);
        }
    }

    public int getSize(){
        return Integer.BYTES + name.length() + (2*Double.BYTES) + (Integer.BYTES) +
                animdata.values().stream().mapToInt(e->e.getSize()).sum();
    }

    public ByteBuffer toBuffer(){
        ByteBuffer b = Allocator.alloc(getSize()).order(ByteOrder.BIG_ENDIAN);
        MLoaderUtils.writeString(this.name,b);
        b.putDouble(duration).putDouble(ticksPerSec).putInt(animdata.size());
        for(AnimNode node:animdata.values()){
            MLoaderUtils.writeString(node.name,b);
            b.putInt(node.positionKeys.size());
            for(Tuple<Double,Vector3f> s:node.positionKeys){
                b.putDouble(s.x).putFloat(s.y.x).putFloat(s.y.y).putFloat(s.y.z);
            }
            b.putInt(node.rotationKeys.size());
            for(Tuple<Double,Quaternionf> s:node.rotationKeys){
                b.putDouble(s.x).putFloat(s.y.w).putFloat(s.y.x).putFloat(s.y.y).putFloat(s.y.z);
            }
            b.putInt(node.scalingKeys.size());
            for(Tuple<Double,Vector3f> s:node.scalingKeys){
                b.putDouble(s.x).putFloat(s.y.x).putFloat(s.y.y).putFloat(s.y.z);
            }
        }
        return b.flip();
    }

    public static class AnimNode{
        public ArrayList<Tuple<Double,Vector3f>> positionKeys;
        public ArrayList<Tuple<Double,Quaternionf>> rotationKeys;
        public ArrayList<Tuple<Double,Vector3f>> scalingKeys;
        public String name;
        public AnimNode(ArrayList<Tuple<Double, Vector3f>> positionKeys, ArrayList<Tuple<Double, Quaternionf>> rotationKeys, ArrayList<Tuple<Double, Vector3f>> scalingKeys, String name) {
            this.positionKeys = positionKeys;
            this.rotationKeys = rotationKeys;
            this.scalingKeys = scalingKeys;
            this.name = name;
        }

        public AnimNode(ByteBuffer b){
            name = MLoaderUtils.readString(b);
            int numPos = b.getInt();
            positionKeys = new ArrayList<>(numPos);
            for(int i=0;i<numPos;i++)
                positionKeys.add(new Tuple(b.getDouble(),
                        new Vector3f(b.getFloat(),b.getFloat(),b.getFloat())));
            numPos = b.getInt();
            rotationKeys = new ArrayList<>(numPos);
            for(int i=0;i<numPos;i++)
                rotationKeys.add(new Tuple(b.getDouble(),
                        new Quaternionf(b.getFloat(),b.getFloat(),b.getFloat(),b.getFloat())));
            numPos = b.getInt();
            scalingKeys = new ArrayList<>(numPos);
            for(int i=0;i<numPos;i++)
                scalingKeys.add(new Tuple(b.getDouble(),
                        new Vector3f(b.getFloat(),b.getFloat(),b.getFloat())));


        }
        public int getSize(){
            return 4*Integer.BYTES + name.length() + positionKeys.size()*(Double.BYTES + 3*Float.BYTES)
                    + rotationKeys.size()*(Double.BYTES + 4*Float.BYTES)
                    +scalingKeys.size()*(Double.BYTES + 3*Float.BYTES);
        }
    }

}
