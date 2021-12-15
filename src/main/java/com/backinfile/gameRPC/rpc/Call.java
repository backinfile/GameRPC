package com.backinfile.gameRPC.rpc;

import com.backinfile.mrpc.serilize.ISerializable;
import com.backinfile.mrpc.serilize.InputStream;
import com.backinfile.mrpc.serilize.OutputStream;
import com.backinfile.mrpc.utils.Time2;

public class Call {
	public long id;
	public CallPoint from;
	public CallPoint to;

	public int type = RPC_TYPE_CALL;
	public long responseId;

	public int method;
	public Object[] args = null;
	public long expireTime = 0L;

	public static final int RPC_TYPE_CALL = 0;
	public static final int RPC_TYPE_CALL_RETURN = 1;
	public static final int RPC_TYPE_CALL_RETURN_ERROR = 2;

	public Call() {
	}

	public Call newCallReturn(Object[] results) {
		Call callReturn = new Call();
		callReturn.from = to;
		callReturn.to = from;
		callReturn.id = id;
		callReturn.args = results;
		callReturn.type = RPC_TYPE_CALL_RETURN;
		return callReturn;
	}

	public Call newErrorReturn(int code, String error) {
		Call callReturn = new Call();
		callReturn.from = to;
		callReturn.to = from;
		callReturn.id = id;
		callReturn.args = new Object[] { "error", error };
		callReturn.type = RPC_TYPE_CALL_RETURN_ERROR;
		return callReturn;
	}

	public boolean isExpired() {
		if (expireTime > 0)
			return Time2.getCurrentTimestamp() > expireTime;
		return false;
	}

	@Override
	public void writeTo(OutputStream out) {
		out.write(from);
		out.write(to);
		out.write(method);
		out.write(args);
		out.write(expireTime);
		out.write(id);
		out.write(type);
	}

	@Override
	public void readFrom(InputStream in) {
		from = in.read();
		to = in.read();
		method = in.read();
		args = in.read();
		expireTime = in.read();
		id = in.read();
		type = in.read();
	}
}
