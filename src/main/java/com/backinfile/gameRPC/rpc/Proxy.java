package com.backinfile.gameRPC.rpc;

public class Proxy {
    /**
     * 发起rpc请求的原生接口，一般不直接使用
     */
    public static Call rpcRequest(String targetNodeId, String targetPortId, int methodKey, Object[] args) {
        Port port = Port.getCurrentPort();
        if (port == null) {
            throw new SysException("rpc需要在port线程中发起");
        }
        port.getTerminal().sendNewCall(new CallPoint(targetNodeId, targetPortId), methodKey, args);
        return port.getTerminal().getLastOutCall();
    }
}
