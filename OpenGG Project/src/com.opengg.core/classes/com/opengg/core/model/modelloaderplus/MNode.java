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


import java.util.ArrayList;

/**
 *
 * @author warre
 */
public class MNode {
    public Tuple<Double, Vector3f>[] positionkeys;
    public Tuple<Double, Quaternionf>[] rotationkeys;
    public Tuple<Double, Vector3f>[] scalingkeys;
    public String name;

    @Override
    public String toString() {
        return "MNode{" +
                "name='" + name + '\'' +
                ", transform=" + transform +
                '}';
    }

    public Matrix4f transform;
    public MNode parent;
    public ArrayList<MNode> children = new ArrayList<>();
}
