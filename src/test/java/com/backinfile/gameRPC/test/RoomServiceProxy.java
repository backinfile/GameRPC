package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Port;
import com.backinfile.gameRPC.rpc.SysException;

public class RoomServiceProxy {
    private RoomServiceProxy() {
    }


    public static EnterFuture enter(long humanId) {
        Port localPort = Port.getCurrentPort();
        if (localPort == null) {
            throw new SysException("rpc only be called in port thread");
        }
        CallPoint from = new CallPoint(localPort.getNode().getId(), localPort.getId());
    }

    public static class EnterFuture {

    }
}
