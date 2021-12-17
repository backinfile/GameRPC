package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.HashMap;


@SuppressWarnings("unchecked")
public class MapResult implements IResult {

    private int errorCode = 0;
    private HashMap<String, Object> mapValues = null;

    /**
     * 仅供序列化使用
     */
    public MapResult() {
    }

    public MapResult(Object[] values) {
        addValues(values);
    }

    public void addValues(Object[] values) {
        if (values.length > 0 && values.length % 2 == 0) {
            if (mapValues == null) {
                mapValues = new HashMap<>();
            }
            for (int i = 0; i < values.length; i += 2) {
                Object first = values[i];
                Object second = values[i + 1];
                if (!(first instanceof String))
                    break;
                mapValues.put((String) first, second);
            }
        }
    }

    @Override
    public <T> T getResult(String key) {
        if (mapValues != null) {
            return (T) mapValues.get(key);
        }
        return null;
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
        out.write(mapValues);
    }

    @Override
    public void readFrom(InputStream in) {
        errorCode = in.read();
        mapValues = in.read();
    }
}
