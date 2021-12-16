package com.backinfile.gameRPC.support;

import com.backinfile.gameRPC.support.func.Action1;
import com.backinfile.gameRPC.support.func.Function1;
import com.backinfile.gameRPC.support.func.Predicate;

import java.util.*;

public class StreamUtils {
    public static <T, R> List<R> map(Collection<T> collection, Function1<R, T> function) {
        List<R> result = new ArrayList<>(collection.size());
        for (T t : collection) {
            result.add(function.invoke(t));
        }
        return result;
    }

    public static <T, R> void map(Collection<T> collection, Action1<T> function) {
        for (T t : collection) {
            function.invoke(t);
        }
    }

    public static <T> List<T> filter(Collection<T> collection, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T t : collection) {
            if (predicate.test(t)) {
                result.add(t);
            }
        }
        return result;
    }

    public static <T> boolean any(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection) {
            if (predicate.test(t)) {
                return true;
            }
        }
        return false;
    }

    public static <T> boolean all(Collection<T> collection, Predicate<T> predicate) {
        for (T t : collection) {
            if (!predicate.test(t)) {
                return false;
            }
        }
        return true;
    }

    public static <T> T get(Collection<T> collection, int index) {
        if (collection instanceof List) {
            return ((List<T>) collection).get(index);
        }
        Iterator<T> iterator = collection.iterator();
        for (int i = 0; i < index; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    public static <K, V> V getOrDefault(Map<K, V> map, K key, V defaultValue) {
        if (map.containsKey(key)) {
            return map.get(key);
        }
        return defaultValue;
    }

    public static <T> void removeIf(Collection<T> list, Predicate<T> predicate) {
        for (Iterator<T> iterator = list.iterator(); iterator.hasNext(); ) {
            T t = iterator.next();
            if (predicate.test(t)) {
                iterator.remove();
            }
        }
    }
}
