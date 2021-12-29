package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.serialize.SerializableManager;
import com.backinfile.support.Utils;

public class TestServer {

    public static void main(String[] args) {
        SerializableManager.registerAll();
        Node node = new Node("server");
        node.addPort(new RoomService());
        node.addPort(new LoginService());
        node.startUp();
        Utils.readExit();
    }
}
