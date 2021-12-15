package com.backinfile.support.func;

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}
