package com.opengg.core.math;

public class UnorderedTuple<X> extends Tuple<X, X> {

    public UnorderedTuple(X x, X x2) {
        super(x, x2);
    }

    public static <T> UnorderedTuple<T> ofUnordered(T t1, T t2){
        return new UnorderedTuple<>(t1,t2);
    }

    @Override
    public int hashCode() {
        int hash = 1;
        hash = 31 * hash + x.hashCode() + y.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(!(obj instanceof UnorderedTuple)) return false;

        var tuple = (Tuple) obj;

        return (this.x.equals(tuple.x) && this.y.equals(tuple.y)) ||
                (this.x.equals(tuple.y) && this.y.equals(tuple.x));
    }
}
