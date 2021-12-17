package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.support.Time2;
import com.backinfile.gameRPC.support.func.Action1;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * rpc终端--port
 */
public class Terminal implements ITerminal {
    private final ConcurrentLinkedQueue<Call> callCacheList = new ConcurrentLinkedQueue<>();
    private final HashMap<Long, WaitResult> waitingResponseList = new HashMap<>();
    private final Queue<Call> calls = new LinkedList<>();
    private Call lastInCall;
    private Call lastOutCall;
    private long call_id_max = 0;
    private final Node mNode;
    private final Port mPort;

    public static final long CALL_EXPIRE_TIME = 30 * Time2.SEC;
    public static final long LISTEN_EXPIRE_TIME = Time2.SEC;

    public Terminal(Port port, Node node) {
        this.mPort = port;
        this.mNode = node;
    }

    @Override
    public void addCall(Call call) {
        callCacheList.add(call);
    }

    @Override
    public Call getLastInCall() {
        return lastInCall;
    }

    public Call getLastOutCall() {
        return lastOutCall;
    }

    @Override
    public void sendNewCall(CallPoint to, int method, Object[] args) {
        Call call = new Call();
        call.from = Node.getCurCallPoint();
        call.to = to;
        call.id = call_id_max++;
        call.expireTime = mNode.getTime() + CALL_EXPIRE_TIME;
        call.method = method;
        call.args = args;

        lastOutCall = call;
        mNode.handleSendCall(call);
    }

    @Override
    public void returns(Object... values) {
        returns(lastInCall, values);
    }

    @Override
    public void returnsError(int errorCode, String error) {
        returnsError(lastInCall, errorCode, error);
    }

    @Override
    public void returns(Call call, Object... results) {
        Call callReturn = call.newCallReturn(results);
        mNode.handleSendCall(callReturn);
    }

    @Override
    public void returnsError(Call call, int errorCode, String error) {
        Call callReturn = call.newErrorReturn(errorCode, error);
        mNode.handleSendCall(callReturn);
    }

    @Override
    public void checkCallReturnTimeout() {
        flush();

        // 检查发出
        while (true) {
            Call call = calls.peek();
            if (call == null)
                break;
            if (call.isExpired()) {
                calls.poll();
                returnsError(call, ErrorCode.RPC_CALL_TIMEOUT, "rpc调用超时，已忽略");
            } else {
                break;
            }
        }

        // 检查返回
        for (var iter = waitingResponseList.entrySet().iterator(); iter.hasNext(); ) {
            var pair = iter.next();
            if (pair.getValue().isExpire()) {
                iter.remove();
            }
        }
    }

    private void flush() {
        while (true) {
            Call call = callCacheList.poll();
            if (call == null)
                break;
            calls.add(call);
        }
    }

    @Override
    public void listenLastOutCall(Action1<IResult> callback, Object... contexts) {
        if (lastOutCall == null) {
            Log.core.error("没有上次调用， 不能监听");
            return;
        }
        WaitResult waitResult = waitingResponseList.get(lastOutCall.id);
        if (waitResult == null) {
            waitResult = new WaitResult();
            waitingResponseList.put(lastOutCall.id, waitResult);
        }
        waitResult.callbackHandlers.add(callback);
        waitResult.contexts.merge(new Params(contexts));
    }

    public void listenCall(long callId, Action1<IResult> callback) {
        WaitResult waitResult = waitingResponseList.get(callId);
        if (waitResult == null) {
            waitResult = new WaitResult();
            waitingResponseList.put(callId, waitResult);
        }
        waitResult.callbackHandlers.add(callback);
    }

    public void listenCallError(long callId, Action1<IResult> callback) {
        WaitResult waitResult = waitingResponseList.get(callId);
        if (waitResult == null) {
            waitResult = new WaitResult();
            waitingResponseList.put(callId, waitResult);
        }
        waitResult.errorHandlers.add(callback);
    }

    public void listenCallAddContext(long callId, Params params) {
        WaitResult waitResult = waitingResponseList.get(callId);
        if (waitResult == null) {
            waitResult = new WaitResult();
            waitingResponseList.put(callId, waitResult);
        }
        waitResult.contexts.merge(params);
    }

    @Override
    public void executeInCall() {
        flush();
        while (true) {
            Call call = calls.poll();
            if (call == null)
                break;
            try {
                if (call.type == ConstRPC.RPC_TYPE_CALL) {
                    invoke(call);
                } else if (call.type == ConstRPC.RPC_TYPE_CALL_RETURN) {
                    processCallReturn(call);
                } else if (call.type == ConstRPC.RPC_TYPE_CALL_RETURN_ERROR) {
                    processErrorCallReturn(call);
                }
            } catch (Exception e) {
                Log.core.error("error in execute inCall", e);
            }
        }
    }

    private void invoke(Call call) {
        lastInCall = call;
        mPort.handleRequest(call.method, new Params(call.args));
    }

    private void processCallReturn(Call call) {
        if (!waitingResponseList.containsKey(call.id)) {
            return;
        }
        WaitResult waitResult = waitingResponseList.remove(call.id);
        MapResult result = new MapResult(call.args);
        result.setError(false);
        result.updateContexts(waitResult.contexts.getValues());
        for (var callback : waitResult.callbackHandlers) {
            try {
                callback.invoke(result);
            } catch (Exception e) {
                Log.Core.error("run rpc result callbackHandler function error", e);
            }
        }
    }

    private void processErrorCallReturn(Call call) {
        if (!waitingResponseList.containsKey(call.id)) {
            return;
        }
        WaitResult waitResult = waitingResponseList.remove(call.id);
        MapResult result = new MapResult(call.args);
        result.setError(true);
        result.updateContexts(waitResult.contexts.getValues());
        for (var callback : waitResult.errorHandlers) {
            try {
                callback.invoke(result);
            } catch (Exception e) {
                Log.Core.error("run rpc result errorHandler function error", e);
            }
        }
    }
}
