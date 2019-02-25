/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.util;

import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Javier
 */
public class ArrayUtil {

    public static void putInt(int value, byte[] array, int offset) {
        array[offset] = (byte) (0xff & (value >> 24));
        array[offset + 1] = (byte) (0xff & (value >> 16));
        array[offset + 2] = (byte) (0xff & (value >> 8));
        array[offset + 3] = (byte) (0xff & value);
    }

    public static int getInt(byte[] array, int offset) {
        return ((array[offset] & 0xff) << 24)
                | ((array[offset + 1] & 0xff) << 16)
                | ((array[offset + 2] & 0xff) << 8)
                | (array[offset + 3] & 0xff);
    }

    public static void putLong(long value, byte[] array, int offset) {
        array[offset] = (byte) (0xff & (value >> 56));
        array[offset + 1] = (byte) (0xff & (value >> 48));
        array[offset + 2] = (byte) (0xff & (value >> 40));
        array[offset + 3] = (byte) (0xff & (value >> 32));
        array[offset + 4] = (byte) (0xff & (value >> 24));
        array[offset + 5] = (byte) (0xff & (value >> 16));
        array[offset + 6] = (byte) (0xff & (value >> 8));
        array[offset + 7] = (byte) (0xff & value);
    }

    public static long getLong(byte[] array, int offset) {
        return ((long) (array[offset] & 0xff) << 56)
                | ((long) (array[offset + 1] & 0xff) << 48)
                | ((long) (array[offset + 2] & 0xff) << 40)
                | ((long) (array[offset + 3] & 0xff) << 32)
                | ((long) (array[offset + 4] & 0xff) << 24)
                | ((long) (array[offset + 5] & 0xff) << 16)
                | ((long) (array[offset + 6] & 0xff) << 8)
                | ((long) (array[offset + 7] & 0xff));
    }

    public static <T> T getRandom(Collection<T> list){
        if(list.size() == 0) return null;

        var random = new Random().nextInt(list.size());
        T target = null;
        int i = 0;
        var iterator = list.iterator();
        while (iterator.hasNext()){
            target = iterator.next();
            if(i == random) return target;
        }

        return target;
    }


    private ArrayUtil() {
    }

}
