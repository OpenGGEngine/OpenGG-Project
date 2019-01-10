package com.opengg.core.util;

public class LambdaContainer<E>{
    public LambdaContainer(){};
    public LambdaContainer(E e){
        this.value = e;
    }

    public E value;

    public static <T>  LambdaContainer<T> create(){
        return new LambdaContainer<>();
    }

    public static <T> LambdaContainer<T> encapsulate(T t){
        return new LambdaContainer<>(t);
    }
}
