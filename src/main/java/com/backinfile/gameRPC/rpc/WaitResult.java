package com.backinfile.gameRPC.rpc;

import com.backinfile.support.Time2;
import com.backinfile.support.func.Action1;

import java.util.ArrayList;
import java.util.List;

public class WaitResult {
    public long expireTime = 0;
    public List<Callback> callbackHandlers = new ArrayList<>();

    public static class Callback {
        public Action1<IResult> action;
        public Params contexts;
    }

    public void addCallback(Action1<IResult> action, Object... contexts) {
        Callback callback = new Callback();
        callback.action = action;
        callback.contexts = new Params(contexts);
    }

    public boolean isExpire() {
        return expireTime > 0 && expireTime < Time2.getCurMillis();
    }
}
