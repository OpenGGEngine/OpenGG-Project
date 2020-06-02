package com.opengg.core.math.util;

/**
 * Represents an immutable two-element tuple
 * @param <X>
 * @param <Y>
 */
public interface Tuple<X, Y> {
    X x();
    Y y();

    static <X,Y> OrderedTuple<X,Y> of(X x, Y y){
        return new OrderedTuple<>(x,y);
    }

    /**
     * Returns an unordered tuple containing the two elements given <br>
     *     Unordered tuples are equal regardless of the position of the elements
     *     (eg. equals() returns true if first.x1 == second.x2 and first.x2 == second.x1)
     * @param x
     * @param x2
     * @param <X>
     * @return
     */
    static <X> UnorderedTuple<X> ofUnordered(X x, X x2){
        return new UnorderedTuple<>(x,x2);
    }

    record OrderedTuple<X, Y>(X x, Y y) implements Tuple<X,Y> { }

    record UnorderedTuple<X>(X x, X x2) implements Tuple<X, X>  {
        @Override
        public X y() {
            return x2;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof UnorderedTuple<?> tuple){
                return tuple.x.equals(this.x) && tuple.x2.equals(this.x2) || tuple.x.equals(this.x2) && tuple.x2.equals(this.x);
            }else{
                return false;
            }
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = 31 * hash + x.hashCode() + x2.hashCode();
            return hash;
        }

    }
}


