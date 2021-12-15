package com.backinfile.support;

public interface IAlive {
    void start();

    void update(long timeDelta);

    void dispose();
}
