package io.github.agroportal.ncboproxy.util;

@FunctionalInterface
public interface TriFunction<T,U,V,R> {
    R apply(T t,U u,V v);
}
