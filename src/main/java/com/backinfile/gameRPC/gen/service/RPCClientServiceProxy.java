package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.struct.*;

public class RPCClientServiceProxy {
    private final String targetNodeId;
    private final String targetPortId;

    private RPCClientServiceProxy(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static RPCClientServiceProxy newInstance(String targetNodeId, String targetPortId) {
        return new RPCClientServiceProxy(targetNodeId, targetPortId);
    }

    public static RPCClientServiceProxy newInstance(String targetPortId) {
        return new RPCClientServiceProxy(Node.Instance.getId(), targetPortId);
    }

    public static RPCClientServiceProxy newInstance() {
        return new RPCClientServiceProxy(Node.Instance.getId(), AbstractRPCClientService.PORT_ID_PREFIX);
    }



}

