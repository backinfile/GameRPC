package com.backinfile.gameRPC.rpc;

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

    public Params(Params params) {
        this.values.putAll(params.values);
    }

    public Params(Object... params) {
        addValues(params);
    }

    public void addValues(Object... params) {
        assert params.length % 2 == 0;

        for (int i = 0; i < params.length; i += 2) {
            Object key = params[i];
            Object value = params[i + 1];
            assert key instanceof String;
            setValue((String) key, value);
        }
    }

    public Object[] getValues() {
        List<Object> ret = new ArrayList<>(values.size() * 2);
        for (Map.Entry<String, Object> pair : values.entrySet()) {
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
        for (Map.Entry<String, Object> pair : other.values.entrySet()) {
            values.put(pair.getKey(), pair.getValue());
        }
        return this;
    }

    public Params copy() {
        return new Params(this);
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
