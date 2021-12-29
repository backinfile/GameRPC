package com.backinfile.gameRPC.rpc;

public class ConstRPC {
    public static final int RPC_TYPE_CALL = 0;
    public static final int RPC_TYPE_CALL_RETURN = 1; // 与回复的call同id

    public static final int RPC_CODE_OK = 0;
    public static final int RPC_CODE_TIME_OUT = -1; // rpc监听超时
}
