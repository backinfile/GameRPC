package com.backinfile.gameRPC.gen.service;

import com.backinfile.gameRPC.rpc.*;
import com.backinfile.support.func.Action2;
import com.backinfile.gameRPC.gen.GameRPCGenFile;
import com.backinfile.gameRPC.gen.struct.*;

/**
 * client表示这个字段是客户端表示符， 来自客户端的消息需要这个字段才能访问
 * 客户端请求这个rpc时，由框架自动填入这个字段
 * 如果rpc没有这个字段，则客户端不能访问
 */
@GameRPCGenFile
public class RoomServiceProxy {
    private final String targetNodeId;
    private final String targetPortId;

    private RoomServiceProxy(String targetNodeId, String targetPortId) {
        this.targetNodeId = targetNodeId;
        this.targetPortId = targetPortId;
    }

    public static RoomServiceProxy newInstance(String targetNodeId, String targetPortId) {
        return new RoomServiceProxy(targetNodeId, targetPortId);
    }

    public static RoomServiceProxy newInstance(String targetPortId) {
        return new RoomServiceProxy(Node.Instance.getId(), targetPortId);
    }

    public static RoomServiceProxy newInstance() {
        return new RoomServiceProxy(Node.Instance.getId(), AbstractRoomService.PORT_ID_PREFIX);
    }


    /**
     * 登陆
     * 是一个来自客户端的rpc
     */
    @RPCMethod(client = true)
    public LoginFuture login(String name, boolean local) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractRoomService.M.LOGIN_STRING_BOOLEAN, new Object[]{name, local});
        return new LoginFuture(Port.getCurrentPort(), call.id);
    }

    /**
     * 开始游戏
     */
    @RPCMethod(client = true)
    public StartGameFuture startGame() {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractRoomService.M.START_GAME, new Object[]{});
        return new StartGameFuture(Port.getCurrentPort(), call.id);
    }

    @RPCMethod
    public GetHumanInfoFuture getHumanInfo(long id) {
        Call call = Proxy.rpcRequest(targetNodeId, targetPortId, AbstractRoomService.M.GET_HUMAN_INFO_LONG, new Object[]{id});
        return new GetHumanInfoFuture(Port.getCurrentPort(), call.id);
    }


    @FunctionalInterface
    public interface ILoginFutureListener {
        void onResult(int code, String message, boolean online, Params context);
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
                    listener.onResult(r.getResult(0), r.getResult(1), r.getResult(2), contextParams.copy());
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

    @FunctionalInterface
    public interface IStartGameFutureListener {
        void onResult(Params context);
    }

    public static class StartGameFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private StartGameFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public StartGameFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public StartGameFuture then(IStartGameFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public StartGameFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }
    }

    @FunctionalInterface
    public interface IGetHumanInfoFutureListener {
        void onResult(String name, Params context);
    }

    public static class GetHumanInfoFuture {
        private final Port localPort;
        private final long callId;
        private final Params contextParams = new Params();

        private GetHumanInfoFuture(Port localPort, long callId) {
            this.localPort = localPort;
            this.callId = callId;
        }

        /** 设置Context */
        public GetHumanInfoFuture context(Object... context) {
            this.contextParams.addValues(context);
            return this;
        }

        /** 监听返回事件 */
        public GetHumanInfoFuture then(IGetHumanInfoFutureListener listener) {
            localPort.getTerminal().listenOutCall(callId, r -> {
                if (r.getErrorCode() == 0) {
                    listener.onResult(r.getResult(0), contextParams.copy());
                }
            });
            return this;
        }

        /** 监听出错事件 */
        public GetHumanInfoFuture error(Action2<Integer, Params> listener) {
            localPort.getTerminal().listenOutCall(callId, result -> {
                if (result.getErrorCode() != 0) {
                    listener.invoke(result.getErrorCode(), contextParams.copy());
                }
            });
            return this;
        }
    }

}

