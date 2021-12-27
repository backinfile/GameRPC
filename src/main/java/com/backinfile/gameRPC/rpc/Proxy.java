package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;

public class Proxy {
    /**
     * 发起rpc请求的原生接口，一般不直接使用
     */
    public static IRequestFuture request(String targetNodeId, String targetPortId, int methodKey, Object[] args) {
        Port port = Port.getCurrentPort();
        if (port == null) {
            Log.core.error("rpc需要在port中发起", new SysException());
            return null;
        }
        port.getTerminal().sendNewCall(new CallPoint(Node.Instance.getId(), targetPortId), methodKey, args);
        return new RequestFuture(port, port.getTerminal().getLastOutCall().id);
    }

}
