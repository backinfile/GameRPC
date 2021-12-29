package com.backinfile.gameRPC.rpc.server;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.gen.service.AbstractLoginService;
import com.backinfile.gameRPC.net.Server;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.Node;
import com.backinfile.support.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 启动一个tpc服务器，监听来自客户端的消息，验证消息，并转发到服务器node
 */
public class LoginService extends AbstractLoginService {
    private final int port;
    private final Queue<Call> clientCachedCalls = new LinkedList<>();

    // token->id
    private final HashMap<String, Long> onlineHumans = new HashMap<>();

    private long idMax = 1;

    public LoginService(int port) {
        super(AbstractLoginService.PORT_ID_PREFIX);
        this.port = port;
    }

    @Override
    public void startup() {
        super.startup();
        Server server = new Server(port);
        server.start();
    }

    @Override
    public void pulse(boolean perSec) {
        while (true) {
            Call call = clientCachedCalls.poll();
            if (call == null) {
                break;
            }
            if (!call.to.nodeID.equals(Node.Instance.getId())) {
                continue;
            }
            String token = call.from.nodeID;
            if (!verifyToken(token)) {
                continue;
            }
            if (!onlineHumans.containsKey(token)) {
                long id = applyId();
                onlineHumans.put(token, id);
                Log.game.info("new human login token:{} id:{}", token, id);
            }

            long id = onlineHumans.get(token);
            Call.LocalCall localCall = call.makeLocalCall();
            localCall.fromClient = true;
            localCall.clientVar = id;
            Node.Instance.handleCall(localCall);
        }
    }

    private long applyId() {
        return idMax++;
    }

    @Override
    public void login(LoginContext context, String token) {

    }

    private boolean verifyToken(String token) {
        return token.length() == Utils.TOKEN_LENGTH;
    }
}
