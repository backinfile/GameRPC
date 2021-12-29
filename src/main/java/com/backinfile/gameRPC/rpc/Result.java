package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;


@SuppressWarnings("unchecked")
public class Result implements IResult {

    private Object[] results = null;
    private int errorCode = 0;

    /**
     * 仅供序列化使用
     */
    public Result() {
    }

    public Result(Object[] values) {
        this.results = values;
    }


    @Override
    public <T> T getResult(String key) {
        if (results == null) {
            return null;
        }
        for (int i = 0; i < results.length - 1; i += 2) {
            var first = results[i];
            var second = results[i + 1];
            if (first instanceof String) {
                if (((String) first).equals(key)) {
                    return (T) second;
                }
            }
        }
        return null;
    }

    @Override
    public <T> T getResult(int index) {
        if (results == null) {
            return null;
        }
        if (index < 0 || index >= results.length) {
            Log.core.error("getResult", new ArrayIndexOutOfBoundsException(""));
            return null;
        }
        return (T) results[index];
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(errorCode);
        out.write(results);
    }

    @Override
    public void readFrom(InputStream in) {
        errorCode = in.read();
        results = in.read();
    }
}
