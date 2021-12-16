package com.backinfile.gameRPC.support.func;

@FunctionalInterface
public interface Function2<R, T1, T2> {
    R invoke(T1 t1, T2 t2);
}
