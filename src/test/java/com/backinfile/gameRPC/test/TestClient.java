package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.serialize.SerializableManager;
import com.backinfile.support.Utils;

public class TestClient {

    public static void main(String[] args) {
        SerializableManager.registerAll();
        Node node = new Node(Utils.getRandomToken());
        node.addPort(new ClientService());
        node.connectServer("server", "127.0.0.1", 8099);
        node.startUp();
        node.join();
    }

}
