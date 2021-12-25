package com.backinfile.gameRPC.rpc;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.serialize.ISerializable;
import com.backinfile.gameRPC.serialize.InputStream;
import com.backinfile.gameRPC.serialize.OutputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Params implements ISerializable {
    private Map<String, Object> values = new HashMap<>();

    public Params() {
    }

    public Params(Object... params) {
        if (params == null)
            return;
        if (params.length == 0)
            return;
        if (params.length % 2 == 0) {
            for (int i = 0; i < params.length; i += 2) {
                Object key = params[i];
                Object value = params[i + 1];
                if (key instanceof String) {
                    setValue((String) key, value);
                } else {
                    Log.core.warn("ignoring param's arg:{} {}", key, value);
                }
            }
        } else {
            Log.core.error("param 参数为奇数", new SysException());
        }
    }

    public Object[] getValues() {
        List<Object> ret = new ArrayList<>(values.size() * 2);
        for (var pair : values.entrySet()) {
            ret.add(pair.getKey());
            ret.add(pair.getValue());
        }
        return ret.toArray();
    }

    public void setValue(String key, Object value) {
        values.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(String key) {
        return (T) values.get(key);
    }

    public Params merge(Params other) {
        for (var pair : other.values.entrySet()) {
            values.put(pair.getKey(), pair.getValue());
        }
        return this;
    }

    @Override
    public void writeTo(OutputStream out) {
        out.write(values);
    }

    @Override
    public void readFrom(InputStream in) {
        values = in.read();
    }
}
