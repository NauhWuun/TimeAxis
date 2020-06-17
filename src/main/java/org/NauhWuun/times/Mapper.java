package org.NauhWuun.times;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Mapper 
{
    private Mapper() {}

    final static Map<KEY, VALUE> c0 = new ConcurrentHashMap<>();

    public static void add(KEY key, VALUE value) {
        c0.put(key, value);
    }

    public static void Clone() {
        if (c0.isEmpty())
            return;

        System.out.println("clone");
        Map<KEY, VALUE> c1 = new ConcurrentHashMap<>(c0);
        Reduce.merge(Bytes.convertToByteArray(TimeAxis.fixRateTime.getAndAdd(30)), c1);
        c0.clear();
    }
}