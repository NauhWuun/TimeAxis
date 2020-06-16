package org.NauhWuun.times;

import java.util.concurrent.ConcurrentSkipListMap;

public final class Mapper 
{
    private Mapper() {}

    final static ConcurrentSkipListMap<KEY, VALUE> c0 = new ConcurrentSkipListMap<>();

    public static void add(KEY key, VALUE value) {
        c0.put(key, value);
    }

    public static void Clone() {
        ConcurrentSkipListMap<KEY, VALUE> c1 = c0.clone();
        if (c1.isEmpty())
            return;
            
        Reduce.merge(Bytes.convertToByteArray(TimeAxis.fixRateTime + 30), c1);
        c0.clear();
    }
}