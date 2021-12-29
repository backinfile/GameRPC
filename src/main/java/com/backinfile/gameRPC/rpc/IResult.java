package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.serialize.ISerializable;

public interface IResult extends ISerializable {
    /**
     * 以key，value形式获取参数
     */
    <T> T getResult(String key);

    /**
     * 获取第index个参数
     */
    <T> T getResult(int index);

    /**
     * 获取错误码， 0表示没有错误
     */
    int getErrorCode();
}
