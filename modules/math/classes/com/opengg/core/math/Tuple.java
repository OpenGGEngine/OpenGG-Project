/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

import java.util.Objects;

/**
 *
 * @author ethachu19
 */
public class Tuple<X, Y> {
    public X x;
    public Y y;

    private Tuple() {
    }

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    public static <X,Y> Tuple<X,Y> of(X x, Y y){
        return new Tuple<>(x,y);
    }
    
    public X getFirst() {
        return x;
    }
    
    public Y getSecond() {
        return y;
    }

    @Override
    public String toString(){
        return "<" + this.x.toString() + "," + this.y.toString()+">";
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof Tuple)) return false;

        var tuple = (Tuple) obj;

        return this.x.equals(tuple.x) && this.y.equals(tuple.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
