/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.system;

import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 *
 * @author Javier
 */
public class GGBufferUtils {
    public static byte[] get(ByteBuffer source){
        if(source.hasArray()){
            return source.array();
        }else{
            source.rewind();
            byte[] array = new byte[source.limit()];
            for(int i = 0; i < array.length; i++){
                array[i] = source.get();
            }
            source.rewind();
            return array;
        }
    }

    private GGBufferUtils() {
    }
}
