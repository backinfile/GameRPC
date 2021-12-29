package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.struct.*;

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

}

