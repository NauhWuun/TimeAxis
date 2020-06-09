package org.NauhWuun.times;

import java.util.concurrent.ConcurrentSkipListMap;

public final class Mapper 
{
    private Mapper() {}

    private final static ConcurrentSkipListMap<KEY, VALUE> c0 = new ConcurrentSkipListMap<>();
    private final static long MAX_TREE_NODES_COUNT = 1024 * 1024 * 50;

    public static void add(KEY key, VALUE value) {
        if (c0.size() >= MAX_TREE_NODES_COUNT) {
            ConcurrentSkipListMap<KEY, VALUE> c01 = c0.clone();
            TimeAxis.executorService.execute(() -> Reduce.merge(c01));
            c0.clear();
        }

        c0.put(key, value);
    }
}