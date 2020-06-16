package org.NauhWuun.times;

import java.util.Comparator;
import java.util.concurrent.ConcurrentSkipListMap;

public final class Mapper 
{
    private Mapper() {}

    final static ConcurrentSkipListMap<KEY, VALUE> c0 = new ConcurrentSkipListMap<>(Comparator.comparingLong(KEY::getTime));

    public static void add(KEY key, VALUE value) {
        c0.put(key, value);
    }

    public static void Clone() {
        if (c0.isEmpty())
            return;

        System.out.println("Clone");

        ConcurrentSkipListMap<KEY, VALUE> c1 = c0.clone();
        Reduce.merge(Bytes.convertToByteArray(TimeAxis.fixRateTime + 30), c1);
        c0.clear();
    }
}