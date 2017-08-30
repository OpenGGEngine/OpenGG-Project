/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.util.GGOutputStream;
import java.io.IOException;
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

    public void writeBuffer(GGOutputStream ds) throws IOException {
        ds.write(jointMatrices.length);
        for (int i = 0; i < jointMatrices.length; i++) {
            Matrix4f tes= jointMatrices[i];
            ds.write(tes);
        }
    }

}
