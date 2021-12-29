package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

@GameRPCGenFile
public class ClientServiceProxy {
    private final String targetNodeId;
    private final String targetPortId;

    private ClientServiceProxy(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static ClientServiceProxy newInstance(String targetNodeId, String targetPortId) {
        return new ClientServiceProxy(targetNodeId, targetPortId);
    }

    public static ClientServiceProxy newInstance(String targetNodeId) {
        return new ClientServiceProxy(targetNodeId, AbstractClientService.PORT_ID_PREFIX);
    }

    public static ClientServiceProxy newInstance() {
        return new ClientServiceProxy(Node.Instance.getId(), AbstractClientService.PORT_ID_PREFIX);
    }



}

