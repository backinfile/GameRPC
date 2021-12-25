package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.support.func.Action1;

public interface IRequestFuture {
    IRequestFuture then(Action1<IResult> callback, Object... context);
}
