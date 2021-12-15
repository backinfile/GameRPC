package com.backinfile.support.func;

@FunctionalInterface
public interface Function1<R, T> {
    R invoke(T t);
}
