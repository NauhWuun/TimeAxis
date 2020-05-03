package org.NauhWuun.times;

import java.util.concurrent.*;

public final class STree
{
    private final CountMinSketch                    cms;
    private final ConcurrentSkipListMap<KEY, VALUE> c0;
    private final long MAX_TREE_NODES_COUNT = 1024 * 1024;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public STree() {
        cms = new CountMinSketch();
        c0  = new ConcurrentSkipListMap<>();
    }

    public void add(KEY key, VALUE value) {
        if (c0.size() == MAX_TREE_NODES_COUNT) {
            ConcurrentSkipListMap<KEY, VALUE> c01 = c0.clone();
            new STable().merge(c01);
            c0.clear();
        }

        cms.setLong(key.getTime());
        cms.setString(value.getValue());
        c0.put(key, value);
    }

    public Object get(KEY key) { return (c0.get(key) != null && hasKey(key)); }

    public boolean get(VALUE value) { return hasValue(value); }

    public void delete(KEY key) { c0.remove(key); }

    private boolean hasKey(KEY key) { return cms.getEstimatedCountLong(key.getTime()) > 0; }
    private boolean hasValue(VALUE value) { return cms.getEstimatedCountString(value.getValue()) > 0; }
}