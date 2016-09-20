/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.math;

/**
 *
 * @author Javier
 */
public class Sort {
    
    private Sort(){
    }
    
    public Comparable[] bubble(Comparable[] c){
        boolean complete = false;
        while(!complete){
            complete = true;
            for(int i = 0; i < c.length; i++){
                if(c[i].compareTo(c[i+1]) > 0){
                    complete = false;
                    Comparable temp = c[i];
                    c[i] = c[i+1];
                    c[i+1] = temp;
                }
            }
        }
        return c;
    }
    
}
