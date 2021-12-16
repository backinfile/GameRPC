package com.backinfile.gameRPC.support.func;

@FunctionalInterface
public interface Action1<T> {
	void invoke(T t);
}
