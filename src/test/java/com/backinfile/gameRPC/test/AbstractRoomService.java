package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.Port;

public abstract class AbstractRoomService extends Port {

    private static class M {
        public static final int ENTER_L = 0;
    }

    public AbstractRoomService(String portId) {
        super(portId);
    }

    @Override
    public void startup() {
    }

    @Override
    public void casePulseBefore() {
    }

    @Override
    public void casePulse() {
    }

    @Override
    public void casePulseAfter() {
    }

    @Override
    public void handleRequest(int requestKey, Object[] args) {
        switch (requestKey) {
            case M.ENTER_L:
                enter((long) args[0]);
        }
    }

    public abstract void enter(long humanId);
}
