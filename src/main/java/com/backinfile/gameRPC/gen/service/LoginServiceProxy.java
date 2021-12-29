package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

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
    public TestRPCFuture testRPC() {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.TESTRPC, new Object[]{});
        return new TestRPCFuture(Port.getCurrentPort(), call.id);
    }

    @RPCMethod
    public TestAddFuture testAdd(int a, int b) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractLoginService.M.TEST_ADD_INTEGER_INTEGER, new Object[]{a, b});
        return new TestAddFuture(Port.getCurrentPort(), call.id);
    }


    @FunctionalInterface
    public interface ITestRPCFutureListener {
        void onResult(Params context);
    }

    public static class TestRPCFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private TestRPCFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public TestRPCFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public TestRPCFuture then(ITestRPCFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public TestRPCFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
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
    }

}

