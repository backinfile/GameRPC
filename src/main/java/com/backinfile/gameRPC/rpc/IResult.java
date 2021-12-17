package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.serialize.ISerializable;

public interface IResult extends ISerializable {
    public static final String DEFAULT_KEY = "_DEFAULT_KEY_";


    default <T> T getResult() {
        return getResult(DEFAULT_KEY);
    }

    /**
     * 获取参数
     */
    <T> T getResult(String key);

    /**
     * 获取错误码， 0表示没有错误
     */
    int getErrorCode();
}
