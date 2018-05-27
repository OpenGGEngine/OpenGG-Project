/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model.modelloaderplus;
import com.opengg.core.math.Matrix4f;
/**
 *
 * @author warre
 */
public class MBone {
     public String name;
     public int id;
    public Matrix4f offsetMatrix;
    public Matrix4f finalTransformation;
}
