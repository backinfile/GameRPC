package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;

public class Proxy {
    public static IRequestFuture request(String targetPortId, int methodKey, Params params) {
        Port port = Port.getCurrentPort();
        if (port == null) {
            Log.core.error("rpc不在port中执行", new SysException());
            return null;
        }
        port.getTerminal().sendNewCall(new CallPoint(Node.Instance.getId(), targetPortId), methodKey, params.getValues());
        RequestFuture requestFuture = new RequestFuture(port, port.getTerminal().getLastOutCall());
        return requestFuture;
    }

}
