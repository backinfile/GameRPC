package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.HashMap;
import java.util.Map;


@SuppressWarnings("unchecked")
public class MapResult implements IResult {

    private int errorCode = 0;
    private HashMap<String, Object> valueMap = new HashMap<>();
    private HashMap<String, Object> contextMap = new HashMap<>();

    /**
     * 仅供序列化使用
     */
    public MapResult() {
    }

    public MapResult(Object[] values) {
        addValues(values);
    }

    public void addValues(Object[] values) {
        addPairParam(this.valueMap, values);
    }

    public void addContexts(Object[] contexts) {
        addPairParam(this.contextMap, contexts);
    }

    private static void addPairParam(Map<String, Object> paramMap, Object[] values) {
        if (values.length % 2 != 0) {
            Log.core.error("result arg not in pair", new SysException());
            return;
        }
        for (int i = 0; i < values.length; i += 2) {
            Object first = values[i];
            Object second = values[i + 1];
            if (!(first instanceof String)) {
                Log.core.error("result arg type error", new SysException());
                continue;
            }
            paramMap.put((String) first, second);
        }
    }

    public <T> T getContext(String key) {
        return (T) contextMap.get(key);
    }

    @Override
    public <T> T getResult(String key) {
        return (T) valueMap.get(key);
    }

    @Override
    public int getErrorCode() {
        return 0;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(errorCode);
        out.write(valueMap);
    }

    @Override
    public void readFrom(InputStream in) {
        errorCode = in.read();
        valueMap = in.read();
    }
}
