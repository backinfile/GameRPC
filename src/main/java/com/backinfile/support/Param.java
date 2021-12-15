package com.backinfile.support;

import com.backinfile.support.func.Action2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Param {
    public Map<String, Object> valueMap = new HashMap<>();

    public Param() {
    }

    public Param(Object... objects) {
        Assertion.assertTrue(objects.length % 2 == 0);
        for (int i = 0; i < objects.length; i += 2) {
            valueMap.put((String) objects[i], objects[i + 1]);
        }
    }

    public Param(List<Object> list) {
        Assertion.assertTrue(list.size() % 2 == 0);
        for (int i = 0; i < list.size(); i += 2) {
            valueMap.put((String) list.get(i), list.get(i + 1));
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) valueMap.get(key);
    }

    public void put(String key, Object value) {
        valueMap.put(key, value);
    }

    public void clear() {
        valueMap.clear();
    }

    @SuppressWarnings("unchecked")
    public <T> void map(Action2<String, T> action, Class<T> clazz) {
        for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
            if (clazz.isInstance(entry.getValue())) {
                action.invoke(entry.getKey(), (T) entry.getValue());
            }
        }
    }
}
