/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.model.modelloaderplus;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Animation;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.components.RenderComponent;

import java.util.Vector;

import static org.lwjgl.opengl.GL11.glGetError;

/**
 * @author Javier
 */
public class AnimatedComponent extends RenderComponent {
    public Matrix4f 	globalInverseTransform = new Matrix4f();
    public MModel mModel;
    public Matrix4f 	boneTransforms[] = new Matrix4f[200];
    DrawnObjectGroup g = new DrawnObjectGroup();
    long timer = System.currentTimeMillis();

    public AnimatedComponent(MModel model){
        mModel = model;
        g = model.toRenderable();
        globalInverseTransform = model.root.transform;

    }
    MNode FindNodeAnim(String NodeName, MMesh mesh)
    {
        for (int i = 0 ; i < mModel.mnodes.size(); i++) {
            MNode node = mModel.mnodes.get(i);
            if (node.name.equals(NodeName)) return node;
        }

        return null;
    }
    public void render(){
        int i = 0;
        float timecode = (float)((double)System.currentTimeMillis() - (double)timer);
        for(Drawable d : g.objs){
            MMesh mesh = mModel.meshes.get(i);
            boneTransforms((timecode / (float)1000.0),mesh);
            ShaderController.setUniform("jointsMatrix",boneTransforms);
            d.render();
            i++;
        }
    }
    public void update(){
       // boneTransforms((float)(((double)System.currentTimeMillis() - (double)timer) / 1000.0));
    }
    Vector3f CalcInterpolatedPosition(Vector3f Out, float AnimationTime, MNode pNodeAnim)
    {
        if (pNodeAnim.positionkeys.length == 1) {
            Vector3f temp = pNodeAnim.positionkeys[0].y;
            Out = new Vector3f(temp);
            return Out;
        }

        int PositionIndex = FindPosition(AnimationTime, pNodeAnim);
        int NextPositionIndex = (PositionIndex + 1);
        assert(NextPositionIndex < pNodeAnim.positionkeys.length);
        float DeltaTime = (float)(pNodeAnim.positionkeys[NextPositionIndex].x - pNodeAnim.positionkeys[PositionIndex].x);
        float Factor = (AnimationTime - (float)(pNodeAnim.positionkeys[PositionIndex].x.floatValue())) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Vector3f Start = new Vector3f(pNodeAnim.positionkeys[PositionIndex].y);
        Vector3f End = new Vector3f(pNodeAnim.positionkeys[NextPositionIndex].y);
        Vector3f Delta = End.subtract(Start);
        Out = new Vector3f(Start.add(Delta.multiply(Factor)));// + Factor * Delta;
        return Out;
    }


    Quaternionf CalcInterpolatedRotation(Quaternionf Out, float AnimationTime, MNode pNodeAnim)
    {
        // we need at least two values to interpolate...
        if (pNodeAnim.rotationkeys.length == 1) {
            Out = new Quaternionf(pNodeAnim.rotationkeys[0].y);
            return Out;
        }

        int RotationIndex = FindRotation(AnimationTime, pNodeAnim);
        int NextRotationIndex = (RotationIndex + 1);
        assert(NextRotationIndex < pNodeAnim.rotationkeys.length);
        float DeltaTime = (float)(pNodeAnim.rotationkeys[NextRotationIndex].x - pNodeAnim.rotationkeys[RotationIndex].x);
        float Factor = (AnimationTime - (float)pNodeAnim.rotationkeys[RotationIndex].x.floatValue()) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Quaternionf StartRotationQ = new Quaternionf(pNodeAnim.rotationkeys[RotationIndex].y);
        Quaternionf EndRotationQ   = new Quaternionf(pNodeAnim.rotationkeys[NextRotationIndex].y);
        //Out = new Quaternionf(StartRotationQ.SLerp(EndRotationQ, Factor, false));// = AIQuaternion.Interpolate(Out, StartRotationQ, EndRotationQ, Factor);
        Out = StartRotationQ.slerp(EndRotationQ,Factor);
       // Out = Out.normalize();
        return Out;
//        Out = Out.Normalize();
    }


    Vector3f CalcInterpolatedScaling(Vector3f Out, float AnimationTime, MNode pNodeAnim)
    {
        if (pNodeAnim.scalingkeys.length == 1) {
            Out = new Vector3f(pNodeAnim.scalingkeys[0].y);
            return Out;
        }
        int ScalingIndex = FindScaling(AnimationTime, pNodeAnim);
        int NextScalingIndex = (ScalingIndex + 1);
        assert(NextScalingIndex < pNodeAnim.scalingkeys.length);
        float DeltaTime = (float)(pNodeAnim.scalingkeys[NextScalingIndex].x - pNodeAnim.scalingkeys[ScalingIndex].x);
        float Factor = (AnimationTime - (float)pNodeAnim.scalingkeys[ScalingIndex].x.floatValue()) / DeltaTime;
        assert(Factor >= 0.0f && Factor <= 1.0f);
        Vector3f Start = new Vector3f(pNodeAnim.scalingkeys[ScalingIndex].y);
        Vector3f End   = new Vector3f(pNodeAnim.scalingkeys[NextScalingIndex].y);
        Vector3f Delta = End.subtract(Start);
        Out = new Vector3f(Start.add(Delta.multiply(Factor)));
        return Out;
    }

    int FindPosition(float AnimationTime,MNode pNodeAnim)
    {
        for (int i = 0 ; i < pNodeAnim.positionkeys.length - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.positionkeys[i + 1].x.floatValue()) {
                return i;
            }
        }

        return 0;
    }


    int FindRotation(float AnimationTime,  MNode pNodeAnim)
    {
        assert(pNodeAnim.rotationkeys.length > 0);

        for (int i = 0 ; i < pNodeAnim.rotationkeys.length - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.rotationkeys[i + 1].x.floatValue()) {
                return i;
            }
        }

        return 0;
    }


    int FindScaling(float AnimationTime,MNode pNodeAnim)
    {
        assert(pNodeAnim.scalingkeys.length > 0);

        for (int i = 0 ; i < pNodeAnim.scalingkeys.length - 1 ; i++) {
            if (AnimationTime < (float)pNodeAnim.scalingkeys[i + 1].x.floatValue()) {
                return i;
            }
        }

        return 0;
    }

    protected void ReadNodeHeirarchy(float AnimationTime, MNode pNode, Matrix4f ParentTransform,MMesh mesh)
    {
        String NodeName = pNode.name;

        Matrix4f NodeTransformation = new Matrix4f(pNode.transform);

        MNode pNodeAnim = FindNodeAnim(NodeName,mesh);
        if (pNode.positionkeys.length != 0)
        {
            Vector3f Scaling = new Vector3f(0, 0, 0);
            Scaling = CalcInterpolatedScaling(Scaling, AnimationTime, pNodeAnim);
            Matrix4f ScalingM = new Matrix4f().scale(Scaling.x(), Scaling.y(), Scaling.z());

            Quaternionf RotationQ = new Quaternionf(0, 0, 0, 0);
            RotationQ = CalcInterpolatedRotation(RotationQ, AnimationTime, pNodeAnim);
            Matrix4f RotationM = RotationQ.convertMatrix();

            Vector3f Translation = new Vector3f(0, 0, 0);
            Translation = CalcInterpolatedPosition(Translation, AnimationTime, pNodeAnim);
            Matrix4f TranslationM = new Matrix4f().translate(Translation.x(), Translation.y(), Translation.z());

            NodeTransformation = TranslationM.multiply(RotationM).multiply(ScalingM);
        }
        Matrix4f GlobalTransformation = ParentTransform.multiply(NodeTransformation);

        MBone bone = null;

        if ((bone = findBone(NodeName,mesh)) != null)
        {
            bone.finalTransformation = (GlobalTransformation).multiply(bone.offsetMatrix);//globalInverseTransform.multiply(GlobalTransformation).multiply(bone.offsetMatrix);
        }

        for (int i = 0 ; i < pNode.children.size(); i++) {
            ReadNodeHeirarchy(AnimationTime, pNode.children.get(i), GlobalTransformation,mesh);
        }
    }

    private final MBone findBone(String name,MMesh mesh)
    {
            if(mesh.bones != null) {
                for (MBone bone : mesh.bones) {
                    if (bone.name.equals(name)) return bone;
                }
            }

        return null;
    }

    public void boneTransforms(float timeInSeconds,MMesh mesh)
    {
        Matrix4f Identity = new Matrix4f();

        float TicksPerSecond = (float)(mModel.tickspeed != 0 ? mModel.tickspeed : 25.0f);
        float TimeInTicks = timeInSeconds * TicksPerSecond;
        float AnimationTime = (TimeInTicks % (float)mModel.duration);

        ReadNodeHeirarchy(AnimationTime, mModel.root, Identity,mesh);

            if(mesh.bones != null) {
                int s = 0;
                for (MBone bone : mesh.bones) {
                    boneTransforms[bone.id] = bone.finalTransformation;
                }
            }
        System.out.println("---------------frame end-----------");


    }

}
