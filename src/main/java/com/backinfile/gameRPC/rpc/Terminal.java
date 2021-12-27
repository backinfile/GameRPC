package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.support.Time2;
import com.backinfile.gameRPC.support.func.Action1;

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * rpc终端--port
 */
public class Terminal implements ITerminal {
    private final Queue<Call> calls = new ConcurrentLinkedQueue<>(); // 等待执行的Call队列
    private final HashMap<Long, WaitResult> waitingResponseList = new HashMap<>(); // 等待远程返回

    private Call lastInCall; // 上一个接受到的call
    private Call lastOutCall; // 上一个发送出去的call
    private long idAllot = 1; // 分配id
    private final Node mNode;
    private final Port mPort;

    // 监听失效时间
    public static final long CALL_EXPIRE_TIME = 30 * Time2.SEC;

    public Terminal(Node node, Port port) {
        this.mPort = port;
        this.mNode = node;
    }

    // 此terminal接受到新call
    @Override
    public void addCall(Call call) {
        calls.add(call);
    }

    @Override
    public Call getLastInCall() {
        return lastInCall;
    }

    public Call getLastOutCall() {
        return lastOutCall;
    }

    /**
     * 由此terminal发送新call到其他terminal，必须在port线程中发送
     */
    @Override
    public void sendNewCall(CallPoint to, int method, Object[] args) {
        Call call = Call.newCall(applyId(), getLocalCallPoint(), to.copy(), method, args);
        lastOutCall = call;
        mNode.handleCall(call);
    }

    private CallPoint getLocalCallPoint() {
        return new CallPoint(mNode.getId(), mPort.getId());
    }

    private long applyId() {
        return idAllot++;
    }

    @Override
    public void returns(Call call, Object... results) {
        Call callReturn = call.newCallReturn(results);
        mNode.handleCall(callReturn);
    }

    @Override
    public void pulse() {
        // 检查有没有失效的listen
        waitingResponseList.values().removeIf(WaitResult::isExpire);
        executeInCall();
    }

    @Override
    public void listenOutCall(long callId, Action1<IResult> action, Object... context) {
        WaitResult waitResult = waitingResponseList.get(callId);
        if (waitResult == null) {
            waitResult = new WaitResult();
            waitResult.expireTime = Time2.getCurMillis() + CALL_EXPIRE_TIME;
            waitingResponseList.put(callId, waitResult);
        }
        waitResult.addCallback(action, context);
    }

    /**
     * 处理收到的rpc请求
     */
    public void executeInCall() {
        while (true) {
            Call call = calls.poll();
            if (call == null)
                break;
            try {
                if (call.type == ConstRPC.RPC_TYPE_CALL) {
                    invoke(call);
                } else if (call.type == ConstRPC.RPC_TYPE_CALL_RETURN) {
                    processCallReturn(call);
                } else {
                    Log.core.error("unknown rpc call type {}", call.type);
                }
            } catch (Exception e) {
                Log.core.error("error in execute inCall", e);
            }
        }
    }

    /**
     * 前往对应port执行来自远程的调用
     */
    private void invoke(Call call) {
        lastInCall = call;
        mPort.handleRequest(call.method, call.args);
    }

    /**
     * rpc调用返回处理，触发监听事件
     */
    private void processCallReturn(Call call) {
        if (!waitingResponseList.containsKey(call.id)) {
            return;
        }
        WaitResult waitResult = waitingResponseList.remove(call.id);
        for (var callback : waitResult.callbackHandlers) {
            try {
                MapResult result = new MapResult();
                result.setErrorCode(call.code);
                result.addValues(call.args);
                result.addContexts(callback.contexts.getValues());
                callback.action.invoke(result);
            } catch (Exception e) {
                Log.core.error("run rpc result callbackHandler function error", e);
            }
        }
    }
}
