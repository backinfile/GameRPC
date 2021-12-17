package com.backinfile.gameRPC.serialize;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.Params;
import com.backinfile.gameRPC.rpc.Result;
import com.backinfile.gameRPC.support.reflection.ReflectionUtils;
import com.google.protobuf.Message;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 代码生成改为反射形式
 */
public class SerializableManager {

    @SuppressWarnings("unchecked")
    public static <T> T parseFromSerializeID(int id) {
        return (T) idSaves.get(id);
    }

    public static int getCommonSerializeID(Object obj) {
        if (obj instanceof Class) {
            return ((Class<?>) obj).getName().hashCode();
        }
        return obj.getClass().getName().hashCode();
    }

    private static final Map<Integer, Object> idSaves = new HashMap<>();

    public static void registerAll(String packageName) {
        registerAllEnum(packageName);
        registerAllSerialize(packageName);
        registerAllMessage(packageName);
    }

    private static void registerAllSerialize(String packageName) {
        Set<Class<?>> classes = ReflectionUtils.getClassesExtendsClass(packageName, ISerializable.class);
        classes.addAll(registerLocalSerialize());
        for (Class<?> clazz : classes) {
            try {
                int id = getCommonSerializeID(clazz);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                idSaves.put(id, constructor);
            } catch (Exception e) {
                Log.serialize.error("可能是ISerializable接口的实现没有空的构造函数", e);
            }
        }
    }

    private static void registerAllMessage(String packageName) {
        Set<Class<?>> classes = ReflectionUtils.getClassesExtendsClass(packageName, Message.class);
        for (Class<?> clazz : classes) {
            try {
                int id = getCommonSerializeID(clazz);
                Method method = clazz.getDeclaredMethod("newBuilder");
                Object value = method.invoke(null);
                idSaves.put(id, value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void registerAllEnum(String packageName) {
        Set<Class<?>> classes = ReflectionUtils.getClassesExtendsClass(packageName, Enum.class);
        for (Class<?> clazz : classes) {
            try {
                int id = getCommonSerializeID(clazz);
                Method method = clazz.getDeclaredMethod("values");
                Object value = method.invoke(null);
                idSaves.put(id, value);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static Set<Class<?>> registerLocalSerialize() {
        Set<Class<?>> set = new HashSet<>();
        set.add(Call.class);
        set.add(CallPoint.class);
        set.add(Params.class);
        set.add(Result.class);
        return set;
    }
}
