/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

import java.util.LinkedList;

/**
 * OpenGG Optimized Sorting
 * @author Javier
 */
public class Sort {
    
    private Sort(){}
    
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
    
    public static void insertion(Comparable[] c){
        LinkedList<Comparable> c2 = new LinkedList<>();
        c2.add(c[0]);
        jew: for(int i = 1; i < c.length; i++){
            for(int j = 0; j < c2.size(); j++){
                if(c2.get(j).compareTo(c[i]) > 0){
                    c2.add(i, c[i]);
                    continue jew;
                }
                if(j == c2.size() - 1){
                    c2.add(c[i]);
                    continue jew;
                }
            }
        }
        c = new Comparable[c.length];
        for(int i = 0; i < c2.size(); i++){
            c[i] = c2.get(i);
        }
    }
    
    private static void swap(Object i, Object k){
        Object temp = i;
        i = k;
        k = temp;
    }
}
