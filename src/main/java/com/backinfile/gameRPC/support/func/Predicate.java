package com.backinfile.gameRPC.support.func;

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
