package com.backinfile.support;

import java.util.LinkedList;

public abstract class ObjectPool<T> {
	private LinkedList<T> freeObjs = new LinkedList<>();

	public ObjectPool() {
	}

	public int getFreeCount() {
		return freeObjs.size();
	}

	protected abstract T newObject();

	public T obtain() {
		return freeObjs.isEmpty() ? newObject() : freeObjs.pollLast();
	}

	public void free(T obj) {
		freeObjs.addLast(obj);
	}

}
