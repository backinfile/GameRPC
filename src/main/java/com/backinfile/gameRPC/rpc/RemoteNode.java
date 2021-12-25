package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
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

    public abstract void start();

    public void pulse() {
        pulseInput();
    }

    // 接受来自远程的消息, 交由当前Node处理
    private void pulseInput() {
        while (isAlive()) {
            GameMessage gameMessage = connection.pollGameMessage();
            if (gameMessage == null) {
                break;
            }
            Call call = gameMessage.getMessage();
            if (call.to.nodeID.equals(Node.Instance.getId())) {
                Node.Instance.handleCall(call);
            } else {
                Log.core.error("接收到了发送至其他node({})的消息，忽略", call.to.nodeID);
            }
        }
    }

    public void sendMessage(Call call) {
        if (isAlive()) {
            connection.sendGameMessage(GameMessage.build(call));
        }
    }

    public boolean isAlive() {
        return connection != null && connection.isAlive();
    }

    public void close() {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }


    /**
     * 连接远程客户端
     */
    public static class RemoteClient extends RemoteNode {

        @Override
        public void start() {

        }
    }

    /**
     * 连接远程服务器
     */
    public static class RemoteServer extends RemoteNode {
        private final String ip;
        private final int port;
        private Client client;

        public RemoteServer(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }

        @Override
        public void start() {
            client = new Client(this, ip, port);
            client.start();
        }

        @Override
        public void close() {
            super.close();
            if (client != null) {
                client.stopClient();
                client = null;
            }
        }
    }
}
