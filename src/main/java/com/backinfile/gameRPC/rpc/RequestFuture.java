package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.support.func.Action1;

public class RequestFuture implements IRequestFuture {
    private Port curPort;
    private long callId;

    public RequestFuture(Port curPort, long callId) {
        this.curPort = curPort;
        this.callId = callId;
    }

    @Override
    public RequestFuture then(Action1<IResult> callback, Object... context) {
        curPort.getTerminal().listenOutCall(callId, callback, context);
        return this;
    }
}
