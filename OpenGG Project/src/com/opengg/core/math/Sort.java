/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

/**
 * OpenGG Optimized Sorting
 * @author Javier
 */
public class Sort {
    
    private Sort(){
    }
    
    public static void bubble(Comparable[] c){
        boolean complete = false;
        while(!complete){
            complete = true;
            for(int i = 0; i < c.length; i++){
                if(c[i].compareTo(c[i+1]) > 0){
                    complete = false;
                    swap(c[i], c[i+1]);
                }
            }
        }
    }
    
    public static void selection(Comparable[] c){
        int i,j;
        for (j = 0; j < c.length-1; j++) {
            int iMin = j;
            for ( i = j+1; i < c.length; i++) {
                if (c[i].compareTo(c[iMin]) < 0) {
                    iMin = i;

                }
            }
            if(iMin != j) {
                swap(c[j],c[iMin]);
            }

        }
    }   
    
    private static void swap(Object i, Object k){
        Object temp = i;
        i = k;
        k = temp;
    }
}
