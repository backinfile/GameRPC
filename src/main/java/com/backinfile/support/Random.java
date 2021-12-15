package com.backinfile.support;

import java.util.Collection;

public class Random {
    private java.util.Random random;

    public Random() {
        this(Time2.getCurMillis());
    }

    public Random(long seed) {
        random = new java.util.Random(seed);
    }

    public int next(int b) {
        return random.nextInt(b);
    }

    public int next(int a, int b) {
        return random.nextInt(b - a) + a;
    }

    public <T> T randItem(Collection<T> collection) {
        int size = collection.size();
        int index = next(size);
        return StreamUtils.get(collection, index);
    }
}
