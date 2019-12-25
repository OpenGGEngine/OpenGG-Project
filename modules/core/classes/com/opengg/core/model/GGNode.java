package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;

import java.util.ArrayList;

/**
 *
 * @author Warren
 */

public class GGNode {
    public String name;
    public Matrix4f transformation;
    public GGNode parent;
    public ArrayList<GGNode> children = new ArrayList<>();

    //Used only when writing to BMFs.
    //Must manually refresh this value if the node graph is changed
    public int byteSize;

    public GGNode(String name, Matrix4f transformation) {
        this.name = name;
        this.transformation = transformation;
    }
}
