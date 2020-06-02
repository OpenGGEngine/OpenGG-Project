package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.util.Tuple;
import com.opengg.core.math.Vector3f;
import com.opengg.core.system.Allocator;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages skeletal animation interpolation and sets uniforms.
 */

public class GGAnimation {
    public String name;
    public double duration,ticksPerSec,current;

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
                animdata.values().stream().mapToInt(AnimNode::getSize).sum();
    }

    public ByteBuffer toBuffer(){
        ByteBuffer b = Allocator.alloc(getSize()).order(ByteOrder.BIG_ENDIAN);
        MLoaderUtils.writeString(this.name,b);
        b.putDouble(duration).putDouble(ticksPerSec).putInt(animdata.size());
        for(AnimNode node:animdata.values()){
            MLoaderUtils.writeString(node.name,b);
            b.putInt(node.positionKeys.size());
            for(Tuple<Double,Vector3f> s:node.positionKeys){
                b.putDouble(s.x()).putFloat(s.y().x()).putFloat(s.y().y()).putFloat(s.y().z);
            }
            b.putInt(node.rotationKeys.size());
            for(Tuple<Double,Quaternionf> s:node.rotationKeys){
                b.putDouble(s.x()).putFloat(s.y().w).putFloat(s.y().x()).putFloat(s.y().y()).putFloat(s.y().z);
            }
            b.putInt(node.scalingKeys.size());
            for(Tuple<Double,Vector3f> s:node.scalingKeys){
                b.putDouble(s.x()).putFloat(s.y().x()).putFloat(s.y().y()).putFloat(s.y().z);
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
                positionKeys.add(Tuple.of(b.getDouble(),
                        new Vector3f(b.getFloat(),b.getFloat(),b.getFloat())));
            numPos = b.getInt();
            rotationKeys = new ArrayList<>(numPos);
            for(int i=0;i<numPos;i++)
                rotationKeys.add(Tuple.of(b.getDouble(),
                        new Quaternionf(b.getFloat(),b.getFloat(),b.getFloat(),b.getFloat())));
            numPos = b.getInt();
            scalingKeys = new ArrayList<>(numPos);
            for(int i=0;i<numPos;i++)
                scalingKeys.add(Tuple.of(b.getDouble(),
                        new Vector3f(b.getFloat(),b.getFloat(),b.getFloat())));


        }
        public int getSize(){
            return 4*Integer.BYTES + name.length() + positionKeys.size()*(Double.BYTES + 3*Float.BYTES)
                    + rotationKeys.size()*(Double.BYTES + 4*Float.BYTES)
                    +scalingKeys.size()*(Double.BYTES + 3*Float.BYTES);
        }
        public int getCurrPositionIndex(double time){
            if(positionKeys.size() > 0){
                for (int i = 0 ; i < positionKeys.size()-1 ; i++) {
                    if (time < (positionKeys.get(i + 1).x()) ){
                        return i;
                    }
                }
            }
            return 0;
        }
        public int getCurrRotationIndex(double time){
            if(rotationKeys.size() > 0){
                for (int i = 0 ; i < rotationKeys.size()-1 ; i++) {
                    if (time < (rotationKeys.get(i + 1).x()) ){
                        return i;
                    }
                }
            }
            return 0;
        }
        public int getCurrScalingIndex(double time){
            if(scalingKeys.size() > 0){
                for (int i = 0 ; i < scalingKeys.size()-1 ; i++) {
                    if (time < (scalingKeys.get(i + 1).x()) ){
                        return i;
                    }
                }
            }
            return 0;
        }
    }
    public Vector3f calcInterpolatedPosition(AnimNode node){
        if(node.positionKeys.size() == 1){
            return node.positionKeys.get(0).y();
        }
        int index = node.getCurrPositionIndex(current);
        int nextInd = index++;

        double delta = node.positionKeys.get(nextInd).x() - node.positionKeys.get(index).x();
        double interpFactor = (current - node.positionKeys.get(index).x())/delta;
        Vector3f startPos = node.positionKeys.get(index).y();
        Vector3f endPos = node.positionKeys.get(nextInd).y();
        return Vector3f.lerp(startPos,endPos,(float)interpFactor);
    }
    public Quaternionf calcInterpolatedRotation(AnimNode node){
        if(node.rotationKeys.size() == 1){
            return node.rotationKeys.get(0).y();
        }
        int index = node.getCurrRotationIndex(current);
        int nextInd = index++;

        double delta = node.rotationKeys.get(nextInd).x() - node.rotationKeys.get(index).x();
        double interpFactor = (current - node.rotationKeys.get(index).x())/delta;
        Quaternionf startRot = node.rotationKeys.get(index).y();
        Quaternionf endRot = node.rotationKeys.get(nextInd).y();
        return Quaternionf.slerp(startRot,endRot,(float)interpFactor);
    }
    public Vector3f calcInterpolatedScaling(AnimNode node){
        if(node.scalingKeys.size() == 1){
            return node.scalingKeys.get(0).y();
        }
        int index = node.getCurrScalingIndex(current);
        int nextInd = index++;

        double delta = node.scalingKeys.get(nextInd).x() - node.scalingKeys.get(index).x();
        double interpFactor = (current - node.scalingKeys.get(index).x())/delta;
        Vector3f startS = node.scalingKeys.get(index).y();
        Vector3f endS = node.scalingKeys.get(nextInd).y();
        return Vector3f.lerp(startS,endS,(float)interpFactor);
    }
    public void animateUniforms(GGNode node,GGBone[] bones){
        recursive(node, node.transformation,bones);
    }
    private void recursive(GGNode node, Matrix4f transformation,GGBone[] bones){
        Matrix4f globalTransform = transformation.multiply(node.transformation);
        if(animdata.containsKey(node.name)){
            AnimNode n = animdata.get(name);
            Vector3f position = calcInterpolatedPosition(n);
            Quaternionf rot = calcInterpolatedRotation(n);
            Vector3f scaling = calcInterpolatedScaling(n);
            Matrix4f real = new Matrix4f().scale(scaling).rotate(rot).translate(position).multiply(globalTransform);
            for(GGBone bone:bones){
               if(bone.name == node.name){
                   bone.finalTransform = real.multiply(bone.offset);
               }
            }
        }
        for(GGNode child:node.children){
            recursive(child,globalTransform,bones);
        }
    }



}
