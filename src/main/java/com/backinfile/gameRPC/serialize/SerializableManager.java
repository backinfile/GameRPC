package com.backinfile.gameRPC.serialize;

import com.backinfile.gameRPC.Log;
import com.backinfile.gameRPC.rpc.Call;
import com.backinfile.gameRPC.rpc.CallPoint;
import com.backinfile.gameRPC.rpc.MapResult;
import com.backinfile.gameRPC.rpc.Params;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 代码生成改为反射形式
 */
public class SerializableManager {
    private static final Map<Integer, Object> idSaves = new HashMap<>();

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


    public static void registerAll(ClassLoader... classLoaders) {
        Reflections reflections = new Reflections(new SubTypesScanner(false), SerializableManager.class.getClassLoader(), classLoaders);
        registerAllEnum(reflections);
        registerAllSerialize(reflections);

        for (var clazz : reflections.getAllTypes()) {
            Log.serialize.info("test find {}", clazz);
        }
    }

    private static void registerAllSerialize(Reflections reflections) {

        Collection<Class<? extends ISerializable>> classes = reflections.getSubTypesOf(ISerializable.class);
        classes.addAll(registerLocalSerialize());
        for (Class<?> clazz : classes) {
            try {
                int id = getCommonSerializeID(clazz);
                Constructor<?> constructor = clazz.getDeclaredConstructor();
                idSaves.put(id, constructor);
                Log.serialize.info("find class:{}", clazz.getSimpleName());
            } catch (Exception e) {
                Log.serialize.error("可能是ISerializable接口的实现没有空的构造函数", e);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void registerAllEnum(Reflections reflections) {
        Set<Class<? extends Enum>> classes = reflections.getSubTypesOf(Enum.class);
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

    private static Collection<Class<? extends ISerializable>> registerLocalSerialize() {
        Set<Class<? extends ISerializable>> set = new HashSet<>();
        set.add(Call.class);
        set.add(CallPoint.class);
        set.add(Params.class);
        set.add(MapResult.class);
        return set;
    }
}
