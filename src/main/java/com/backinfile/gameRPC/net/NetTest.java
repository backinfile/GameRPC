package com.backinfile.gameRPC.net;

import com.backinfile.gameRPC.support.Utils;

import java.io.IOException;

public class NetTest {

}


class TestClient {
    public static void main(String[] args) throws IOException {
        Utils.readExit();
    }
}

class TestServer {
    public static void main(String[] args) {
        Server server = new Server(10088);
        server.start();

        Utils.readExit();
    }
}