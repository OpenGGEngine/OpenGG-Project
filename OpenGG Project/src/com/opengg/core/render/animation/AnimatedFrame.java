/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.animation;

import com.opengg.core.math.Matrix4f;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Arrays;

public class AnimatedFrame {

    private static final Matrix4f IDENTITY_MATRIX = new Matrix4f();

    public static final int MAX_JOINTS = 50;

    private final Matrix4f[] jointMatrices;

    public AnimatedFrame() {
        jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(jointMatrices, IDENTITY_MATRIX);
    }
     public AnimatedFrame(Matrix4f[] moveon) {
        jointMatrices = moveon;
    }

    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    public void setMatrix(int pos, Matrix4f jointMatrix) {
        jointMatrices[pos] = jointMatrix;
    }

    public void writeBuffer(DataOutputStream ds) throws IOException {
        ds.writeInt(jointMatrices.length);
        for (int i = 0; i < jointMatrices.length; i++) {
            System.out.println("Wrote joint: " + i);
            Matrix4f tes= jointMatrices[i];
            System.out.println(tes);
             ds.writeFloat(tes.m00);ds.writeFloat(tes.m10);ds.writeFloat(tes.m20);ds.writeFloat(tes.m30);
            ds.writeFloat(tes.m01);ds.writeFloat(tes.m11);ds.writeFloat(tes.m21);ds.writeFloat(tes.m31);
            ds.writeFloat(tes.m02);ds.writeFloat(tes.m12);ds.writeFloat(tes.m22);ds.writeFloat(tes.m32);
            ds.writeFloat(tes.m03);ds.writeFloat(tes.m13);ds.writeFloat(tes.m23);ds.writeFloat(tes.m33);
            
        }
    }

}
