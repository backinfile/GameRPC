package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.Port;

public abstract class AbstractRoomService extends Port {
    public static final String PORT_ID_PREFIX = "RoomService";

    public static class M {
        public static final int ENTER_LONG = 0;
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
        Log.server.info("room pulse");
    }

    @Override
    public void casePulseAfter() {
    }

    @Override
    public void handleRequest(int requestKey, Object[] args, boolean fromClient) {
        switch (requestKey) {
            case M.ENTER_LONG:
                enter((long) args[0]);
                break;
            default:
                Log.core.info("unknown requestKey {} for {}", requestKey, this.getClass().getSimpleName());
        }
    }


    public abstract void pulse(boolean perSec);

    public abstract void enter(long humanId);
}
