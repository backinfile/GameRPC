package com.backinfile.gameRPC.rpc.server;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractLoginService;
import com.backinfile.gameRPC.gen.service.LoginServiceProxy;
import com.backinfile.gameRPC.net.Connection;
import com.backinfile.gameRPC.net.GameMessage;
import com.backinfile.gameRPC.net.Server;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.support.Time2;
import com.backinfile.support.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 启动一个tpc服务器，监听来自客户端的消息，验证消息，并转发到服务器node
 */
public class LoginService extends AbstractLoginService {
    private final int port;

    private final Map<String, Connection> connections = new ConcurrentHashMap<>();

    private final List<Connection> waitingVerifyConnectionList = new ArrayList<>();

    public LoginService(int port) {
        super(AbstractLoginService.PORT_ID_PREFIX);
        this.port = port;
    }

    @Override
    public void startup() {
        super.startup();
        Server server = new Server(port);
        server.start();

        timerQueue.applyTimer(Time2.SEC, () -> {
            LoginServiceProxy proxy = LoginServiceProxy.newInstance();
            proxy.verify()
                    .then(context -> {
                        Log.game.info("callback then");
                    })
                    .error((code, context) -> {
                        Log.game.info("callback error");
                    });
        });
    }

    @Override
    public void pulse(boolean perSec) {

        if (perSec) {
            checkIncomingConnection();
        }

        checkIncomingCalls();
    }


    @Override
    public void verify(VerifyContext context, String token) {
    }

    @Override
    public void heartBeat(HeartBeatContext context, String token) {
        context.returns();
    }

    private void checkIncomingCalls() {
        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            Connection connection = entry.getValue();
            String token = entry.getKey();

            // socket关闭，清除连接
            if (!connection.isAlive()) {
                clearConnection(connection);
                continue;
            }

            if (!handleInCall(token, connection)) {
                clearConnection(connection);
            }

        }
    }

    private void checkIncomingConnection() {
        List<Connection> toRemove = new ArrayList<>();
        for (Connection connection : waitingVerifyConnectionList) {
            if (!connection.isAlive()) {
                toRemove.add(connection);
                continue;
            }

            GameMessage gameMessage = connection.pollGameMessage();
            if (gameMessage == null) {
                continue;
            }
            Call call = gameMessage.getMessage();
            if (call == null) {
                toRemove.add(connection);
                continue;
            }
            String token = call.from.nodeID;
            if (!verifyToken(token)) {
                toRemove.add(connection);
                continue;
            }
            connections.put(token, connection);
            Log.game.info("client connected token:{}", token);
        }

        for (Connection connection : toRemove) {
            waitingVerifyConnectionList.remove(connection);
            if (connection.isAlive()) {
                connection.close();
            }
        }
    }

    /**
     * @return true=消息处理完毕 false=有问题连接关闭
     */
    private boolean handleInCall(String token, Connection connection) {
        while (true) {
            GameMessage gameMessage = connection.pollGameMessage();
            if (gameMessage == null) {
                break;
            }
            // 必须是由客户端直接向服务器发起的call
            Call call = gameMessage.getMessage();
            if (call == null || !call.to.nodeID.equals(Node.Instance.getId()) || !call.from.nodeID.equals(token)) {
                Log.game.warn("ignore msg from token:{}", token);
                continue;
            }

            Call.LocalCall localCall = call.makeLocalCall();
            localCall.fromClient = true;
            localCall.clientVar = token;
            Node.Instance.handleCall(localCall);
        }
        return true;
    }

    /**
     * 新增客户端连接，线程安全
     */
    public void addConnection(Connection connection) {
        post(() -> {
            waitingVerifyConnectionList.add(connection);
        });
    }

    /**
     * 清理客户端连接，线程安全
     */
    public void clearConnection(Connection connection) {
        post(() -> {
            waitingVerifyConnectionList.remove(connection);
            connections.values().remove(connection);
            if (connection.isAlive()) {
                connection.close();
            }
        });
    }

    public Map<String, Connection> getConnections() {
        return connections;
    }

    // 校验token格式
    private static boolean verifyToken(String token) {
        return token.length() == Utils.TOKEN_LENGTH;
    }
}
