package com.backinfile.gameRPC.support;

public interface IAlive {
    void start();

    void update(long timeDelta);

    void dispose();
}
