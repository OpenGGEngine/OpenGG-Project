/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.math;

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

    public Object get(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Index cannot be lower than 0");
        }
        if (index > 1 && !(y instanceof Tuple)) {
            throw new IllegalArgumentException("Index cannot be greater than 1");
        }
        if (index == 0) {
            return x;
        } else {
            if (y instanceof Tuple) {
                return ((Tuple) y).get(index - 1);
            } else {
                return y;
            }
        }
    }

    public Tuple makeTuple(Object... x) {
        Tuple res = new Tuple();
        Tuple current = res;
        res.x = x[0];
        for (int i = 1; i < x.length; i++) {
            if (i == x.length - 1) {
                current.y = x[i];
                break;
            }
            current.y = new Tuple();
            current.x = x[i];
        }
        return res;
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
}
