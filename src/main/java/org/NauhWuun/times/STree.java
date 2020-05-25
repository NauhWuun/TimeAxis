package org.NauhWuun.times;

import java.util.concurrent.*;

public final class STree
{
    private final CountMinSketch cms;
    private final ConcurrentSkipListMap<KEY, VALUE> c0;
    private final long MAX_TREE_NODES_COUNT = 1024 * 1024 * 50;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public STree() {
        cms = new CountMinSketch();
        c0  = new ConcurrentSkipListMap<>();
    }

    public void add(KEY key, VALUE value) {
        cms.setString(key.getKey());

        if (c0.size() == MAX_TREE_NODES_COUNT) {
            ConcurrentSkipListMap<KEY, VALUE> c01 = c0.clone();
            executorService.execute(() -> STable.createInstance().merge(c01));
            c0.clear();
        }

        c0.put(key, value);
    }

    public VALUE get(KEY key) { 
        return hasKey(key) ? c0.get(key) : null;
    }

    public void delete(KEY key) { c0.remove(key); }
    private boolean hasKey(KEY key) { return cms.getEstimatedCountString(key.getKey()) > 0; }
}