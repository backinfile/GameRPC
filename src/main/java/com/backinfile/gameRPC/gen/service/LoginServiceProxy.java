package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

/**
 * 登陆服务，当使用客户端-服务器模式时，需要在服务器启用一个登陆服务
 * 当服务器node接受到客户端的消息时，把发来的call转移至此service处理, 进行验证
 * 验证完成后，此service将call推送到服务器node上
 * 客户端标志为long
 */
@GameRPCGenFile
public class LoginServiceProxy {
    private final String targetNodeId;
    private final String targetPortId;

    private LoginServiceProxy(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static LoginServiceProxy newInstance(String targetNodeId, String targetPortId) {
        return new LoginServiceProxy(targetNodeId, targetPortId);
    }

    public static LoginServiceProxy newInstance(String targetPortId) {
        return new LoginServiceProxy(Node.Instance.getId(), targetPortId);
    }

    public static LoginServiceProxy newInstance() {
        return new LoginServiceProxy(Node.Instance.getId(), AbstractLoginService.PORT_ID_PREFIX);
    }


    @RPCMethod
    public LoginFuture login(String token) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.LOGIN_STRING, new Object[]{token});
        return new LoginFuture(Port.getCurrentPort(), call.id);
    }


    @FunctionalInterface
    public interface ILoginFutureListener {
        void onResult(long id, Params context);
    }

    public static class LoginFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private LoginFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public LoginFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public LoginFuture then(ILoginFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(r.getResult(0), contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public LoginFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }
    }

}

