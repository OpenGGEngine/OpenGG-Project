/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render;

/**
 *
 * @author Javier
 */
public class DrawnObjectHandler {
    static int currentOffset = 0;
    public static int getOffset(){
        return currentOffset;
    }
    public static void addToOffset(long offset){

        currentOffset += offset;
    }
}
