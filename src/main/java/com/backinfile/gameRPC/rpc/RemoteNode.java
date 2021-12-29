package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.net.Client;
import com.backinfile.gameRPC.net.Connection;
import com.backinfile.gameRPC.net.GameMessage;

// 连接远程服务器
public class RemoteNode {
    private String nodeId = "";
    private final String ip;
    private final int port;
    private Connection connection;

    public RemoteNode(String nodeId, String ip, int port) {
        this.nodeId = nodeId;
        this.ip = ip;
        this.port = port;
    }

    public void start() {
        Client client = new Client(this, ip, port);
        client.start();
    }

    public String getId() {
        return nodeId;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public void pulse() {
        if (connection.isAlive()) {
            connection.pulse();
            pulseInput();
        }
    }

    // 处理来自远程的消息
    // 无条件接受来自远程服务器的rpc
    protected void pulseInput() {
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

    public boolean isDisconnected() {
        return connection != null && !connection.isAlive();
    }

    public void close() {
        if (connection != null) {
            if (connection.isAlive()) {
                connection.close();
            }
            connection = null;
        }
    }
}
