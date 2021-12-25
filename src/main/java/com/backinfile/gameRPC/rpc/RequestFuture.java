package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.support.func.Action1;

public class RequestFuture implements IRequestFuture {
    private Port curPort;
    private Call call;

    public RequestFuture(Port curPort, Call call) {
        this.curPort = curPort;
        this.call = call;
    }

    @Override
    public RequestFuture then(Action1<IResult> callback, Object... context) {
        curPort.getTerminal().listenOutCall(call, callback, context);
        return this;
    }
}
