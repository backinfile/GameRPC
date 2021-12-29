package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.struct.*;

public class RPCServerServiceProxy {
    private final String targetNodeId;
    private final String targetPortId;

    private RPCServerServiceProxy(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static RPCServerServiceProxy newInstance(String targetNodeId, String targetPortId) {
        return new RPCServerServiceProxy(targetNodeId, targetPortId);
    }

    public static RPCServerServiceProxy newInstance(String targetPortId) {
        return new RPCServerServiceProxy(Node.Instance.getId(), targetPortId);
    }

    public static RPCServerServiceProxy newInstance() {
        return new RPCServerServiceProxy(Node.Instance.getId(), AbstractRPCServerService.PORT_ID_PREFIX);
    }



}

