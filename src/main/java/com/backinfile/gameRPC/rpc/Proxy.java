package com.backinfile.gameRPC.rpc;

import com.backinfile.mrpc.support.Log;

public class Proxy {
    public static IRequestFuture request(String targetPortId, int methodKey, Params params) {
        Port port = Port.getCurrentPort();
        if (port == null) {
            Log.Core.error("rpc不在port中执行", new SysException());
            return null;
        }
        port.getTerminal().sendNewCall(new CallPoint(targetPortId), methodKey, params.getValues());
        RequestFuture requestFuture = new RequestFuture(port, port.getTerminal().getLastOutCall().id);
        return requestFuture;
    }

}
