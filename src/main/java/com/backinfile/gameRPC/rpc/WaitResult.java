package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.support.Time2;
import com.backinfile.gameRPC.support.func.Action1;

import java.util.ArrayList;
import java.util.List;

public class WaitResult {
    public long expireTime = 0;
    public List<Action1<IResult>> callbackHandlers = new ArrayList<>();
    public List<Action1<IResult>> errorHandlers = new ArrayList<>();
    public Params contexts = new Params();

    public boolean isExpire() {
        return expireTime > 0 && expireTime < Time2.getCurMillis();
    }
}
