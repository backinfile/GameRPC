package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.rpc.server.LoginService;
import com.backinfile.gameRPC.serialize.SerializableManager;

public class TestServer {

    public static void main(String[] args) {
        SerializableManager.registerAll();
        Node node = new Node("server");
        node.addPort(new LoginService(8099));
        node.startUp();
        node.join();
    }
}
