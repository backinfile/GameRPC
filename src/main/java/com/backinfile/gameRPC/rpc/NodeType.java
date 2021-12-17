package com.backinfile.gameRPC.rpc;

public enum NodeType {
    Server, // 可连接其他Server，可接受其他的Client
    Client, // 可连接Server
}
