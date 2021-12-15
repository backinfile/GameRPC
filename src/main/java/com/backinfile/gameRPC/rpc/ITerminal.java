package com.backinfile.gameRPC.rpc;


import com.backinfile.mrpc.function.Action1;

public interface ITerminal {

	// 接受一个来自远程的Call
	void addCall(Call call);

	Call getLastInCall();

	void sendNewCall(CallPoint to, int method, Object[] args);

	void returns(Object... values);

	void returnsError(int errorCode, String error);

	void returns(Call call, Object... results);

	void returnsError(Call call, int errorCode, String error);

	void checkCallReturnTimeout();

	void listenLastOutCall(Action1<IResult> consumer, Object... context);

	void executeInCall();
}
