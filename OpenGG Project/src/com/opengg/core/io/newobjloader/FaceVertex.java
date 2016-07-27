/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.io.newobjloader;

import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;

/**
 *
 * @author Warren
 */
public class FaceVertex {
    int index = -1;
    public Vector3f v = new Vector3f();
    public Vector2f t = new Vector2f();
    public Vector3f n = new Vector3f();

    @Override
    public String toString() {
        return v.toString() + "|" + n.toString() + "|" + t.toString();
    }
}
