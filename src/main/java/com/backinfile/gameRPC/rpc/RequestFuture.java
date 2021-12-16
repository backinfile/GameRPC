package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.support.func.Action1;

public class RequestFuture implements IRequestFuture {
    private Port curPort;
    private long callId;

    public RequestFuture(Port curPort, long callId) {
        this.curPort = curPort;
        this.callId = callId;
    }

    public RequestFuture then(Action1<IResult> callback) {
        curPort.getTerminal().listenCall(callId, callback);
        return this;
    }

    public RequestFuture error(Action1<IResult> errorHandler) {
        curPort.getTerminal().listenCallError(callId, errorHandler);
        return this;
    }

    @Override
    public IRequestFuture addContext(Params params) {
        curPort.getTerminal().listenCallAddContext(callId, params);
        return this;
    }

    @Override
    public IRequestFuture addContext(Object... params) {
        curPort.getTerminal().listenCallAddContext(callId, new Params(params));
        return this;
    }
}
