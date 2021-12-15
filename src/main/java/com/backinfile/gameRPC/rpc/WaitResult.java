package com.backinfile.gameRPC.rpc;

import java.util.ArrayList;
import java.util.List;

import com.backinfile.mrpc.function.Action1;
import com.backinfile.mrpc.utils.Time2;

public class WaitResult {
	public long expireTime = 0;
	public List<Action1<IResult>> callbackHandlers = new ArrayList<>();
	public List<Action1<IResult>> errorHandlers = new ArrayList<>();
	public Params contexts = new Params();

	public boolean isExpire() {
		return expireTime > 0 && expireTime < Time2.getCurrentTimestamp();
	}
}
