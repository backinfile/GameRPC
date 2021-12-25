package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.net.Client;
import com.backinfile.gameRPC.net.Connection;
import com.backinfile.gameRPC.net.GameMessage;

public abstract class RemoteNode {
    protected String nodeId = "";
    protected boolean verified = false;
    protected Connection connection;

    public String getId() {
        return nodeId;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isVerified() {
        return verified;
    }

    public abstract void pulse();


    public abstract void sendMessage(Call call);

    public boolean isAlive() {
        return connection != null && connection.isAlive();
    }

    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }


    public static class RemoteClient extends RemoteNode {
        @Override
        public void pulse() {

        }

        @Override
        public void sendMessage(Call call) {

        }
    }

    public static class RemoteServer extends RemoteNode {
        private Client client;

        public RemoteServer(String ip, int port) {
            client = new Client(this, ip, port);
            client.start();
        }

        @Override
        public void pulse() {
            pulseInput();
            pulseOutput();
        }

        @Override
        public void sendMessage(Call call) {

        }

        private void pulseInput() {
            while (isAlive()) {
                GameMessage gameMessage = connection.getGameMessage();
                if (gameMessage == null) {
                    break;
                }

                Call call = gameMessage.getMessage();
                Node.Instance.handleCall(call, true);
            }
        }

        private void pulseOutput() {
        }

    }
}
