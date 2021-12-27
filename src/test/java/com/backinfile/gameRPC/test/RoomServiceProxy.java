package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.gameRPC.support.func.Action1;

public class RoomServiceProxy {
    private RoomServiceProxy() {
    }


    public static IEnterFuture enter(long humanId) {
        Port localPort = Port.getCurrentPort();
        if (localPort == null) {
            throw new SysException("rpc only be called in port thread");
        }
        CallPoint from = new CallPoint(localPort.getNode().getId(), localPort.getId());
        Call call = Proxy.rpcRequest(localPort.getNode().getId(), localPort.getId(), AbstractRoomService.M.ENTER_LONG, new Object[]{humanId});
        IEnterFuture.EnterFuture future = new IEnterFuture.EnterFuture();
    }

    @FunctionalInterface
    public interface IEnterFutureListener {
        void onResult(boolean success);
    }

    public interface IEnterFuture {

        IEnterFuture then(IEnterFutureListener listener);

        IEnterFuture error(Action1<Integer> listener);

        static class EnterFuture implements IEnterFuture {
            public long callId;
            public Port localPort;

            @Override
            public IEnterFuture then(IEnterFutureListener listener) {
                return this;
            }

            @Override
            public IEnterFuture error(Action1<Integer> listener) {
                return this;
            }
        }
    }

}
