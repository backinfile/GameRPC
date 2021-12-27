package com.backinfile.gameRPC.test;

import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.gameRPC.rpc.Port;

public class TestServerClient {
    private static final int SERVER_PORT = 8099;

    public static class TestServer {
        public static void main(String[] args) {
            Node serverNode = new Node("server");
            Port roomService = new RoomService(AbstractRoomService.PORT_ID_PREFIX);
            serverNode.addPort(roomService);
            serverNode.startServer(SERVER_PORT);
        }
    }

    public static class TestClient {
        public static void main(String[] args) {

        }
    }

}
