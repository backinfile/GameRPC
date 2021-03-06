package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

/**
 * 登陆服务，当使用客户端-服务器模式时，需要在服务器启用一个登陆服务
 * 当接受到客户端的消息时，发来的call在此service处理, 进行验证
 * 验证完成后，此service将call推送到服务器node上
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

    public static LoginServiceProxy newInstance(String targetNodeId) {
        return new LoginServiceProxy(targetNodeId, AbstractLoginService.PORT_ID_PREFIX);
    }

    public static LoginServiceProxy newInstance() {
        return new LoginServiceProxy(Node.Instance.getId(), AbstractLoginService.PORT_ID_PREFIX);
    }


    /**
     * 客户端连接成功后立即发送 仅用于身份验证
     */
    @RPCMethod(client = true)
    public VerifyFuture verify() {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.VERIFY, new Object[]{});
        return new VerifyFuture(Port.getCurrentPort(), call.id);
    }

    /**
     * 心跳 用于保持连接
     */
    @RPCMethod(client = true)
    public HeartBeatFuture heartBeat() {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.HEART_BEAT, new Object[]{});
        return new HeartBeatFuture(Port.getCurrentPort(), call.id);
    }

    @RPCMethod(client = true)
    public TestAddFuture testAdd(int a, int b) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.TEST_ADD_INTEGER_INTEGER, new Object[]{a, b});
        return new TestAddFuture(Port.getCurrentPort(), call.id);
    }


    @FunctionalInterface
    public interface IVerifyFutureListener {
        void onResult(Params context);
    }

    public static class VerifyFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private VerifyFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public VerifyFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public VerifyFuture then(IVerifyFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public VerifyFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }

        /** 设置监听失效时间 */
        public VerifyFuture timeout(long timeout) {
            localPort.getTerminal().setTimeout(callId, timeout);
            return this;
        }
    }

    @FunctionalInterface
    public interface IHeartBeatFutureListener {
        void onResult(Params context);
    }

    public static class HeartBeatFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private HeartBeatFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public HeartBeatFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public HeartBeatFuture then(IHeartBeatFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public HeartBeatFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }

        /** 设置监听失效时间 */
        public HeartBeatFuture timeout(long timeout) {
            localPort.getTerminal().setTimeout(callId, timeout);
            return this;
        }
    }

    @FunctionalInterface
    public interface ITestAddFutureListener {
        void onResult(int result, Params context);
    }

    public static class TestAddFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private TestAddFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public TestAddFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public TestAddFuture then(ITestAddFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(r.getResult(0), contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public TestAddFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }

        /** 设置监听失效时间 */
        public TestAddFuture timeout(long timeout) {
            localPort.getTerminal().setTimeout(callId, timeout);
            return this;
        }
    }

}

