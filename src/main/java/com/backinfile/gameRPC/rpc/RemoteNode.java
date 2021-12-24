package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.net.Client;
import com.backinfile.gameRPC.net.Connection;
import com.backinfile.gameRPC.net.GameMessage;

public class RemoteNode {
    protected String nodeId;
    protected NodeConnectType nodeType;
    protected boolean verified = false;

    public boolean isVerified() {
        return verified;
    }

    public boolean isAlive() {
        return false;
    }

    public void pulse() {
    }

    public void close() {

    }

    public static class RemoteClient extends RemoteNode {
        private Connection connection;

        public RemoteClient(Connection connection) {
            this.verified = false;
        }

        public void setConnection() {
            this.connection = connection;
        }

        @Override
        public void pulse() {
            if (connection.isAlive()) {

            }
        }

        @Override
        public boolean isAlive() {
            return connection.isAlive();
        }

    }

    public static class RemoteServer extends RemoteNode {
        private Connection connection;

        public RemoteServer(String ip, int port) {
            nodeType = NodeConnectType.Server;
            verified = true;
            Client client = new Client(this, ip, port);
            client.start();
        }

        public void setConnection(Connection connection) {
            this.connection = connection;
        }

        @Override
        public void pulse() {
            if (isAlive()) {
                while (true) {
                    GameMessage gameMessage = connection.getGameMessage();
                    if (gameMessage == null) {
                        break;
                    }
                    
                }
            }
        }

        @Override
        public boolean isAlive() {
            return connection != null && connection.isAlive();
        }
    }
}
