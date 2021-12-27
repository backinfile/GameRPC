package com.backinfile.gameRPC.rpc;


import com.backinfile.gameRPC.support.func.Action1;

public interface ITerminal {


    /**
     * 接受一个来自远程的调用
     */
    void addCall(Call call);

    /**
     * 获取上一次执行（或正在执行）的rpc调用
     */
    Call getLastInCall();


    /**
     * 获取上一次自身发起的请求的call对象
     * （通常用于多层级rpc嵌套返回）
     */
    Call getLastOutCall();

    /**
     * 发起新的rpc调用
     */
    void sendNewCall(CallPoint to, int method, Object[] args);


    /**
     * rpc返回
     */
    void returns(Call call, Object... results);

    /**
     * 监听rpc调用执行结果
     *
     * @param context 上下文
     */
    void listenOutCall(long callId, Action1<IResult> action, Object... context);


    void pulse();

    /**
     * rpc返回
     */
    default void returns(Object... values) {
        returns(getLastInCall(), values);
    }

    /**
     * 监听rpc调用执行结果
     *
     * @param context 上下文
     */
    default void listenLastOutCall(Action1<IResult> action, Object... context) {
        listenOutCall(getLastOutCall().id, action, context);
    }
}
