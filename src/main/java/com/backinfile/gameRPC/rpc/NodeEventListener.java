package com.backinfile.gameRPC.rpc;

public interface NodeEventListener {
    void active(NodeService nodeService, Node node);
    void inactive(NodeService nodeService, Node node);

    void onDisconnect(NodeService nodeService, Node node);
    void onReconnect(NodeService nodeService, Node node);
}
