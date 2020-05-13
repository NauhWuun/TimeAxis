package org.NauhWuun.times;

import java.util.concurrent.*;

public final class STree
{
    private final CountMinSketch                        cms;
    private final ConcurrentSkipListMap<byte[], byte[]> c0;
    private final long MAX_TREE_NODES_COUNT = 1024 * 1024 * 50;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public STree() {
        cms = new CountMinSketch();
        c0  = new ConcurrentSkipListMap<>();
    }

    public void add(byte[] key, byte[] value) {
        cms.set(key);

        if (c0.size() == MAX_TREE_NODES_COUNT) {
            ConcurrentSkipListMap<byte[], byte[]> c01 = c0.clone();
            
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    STable.createInstance().merge(c01);
                }
            });

            c0.clear();
        }

        c0.put(key, value);
    }

    public Object get(String key) { return (c0.get(key.getBytes()) != null || hasKey(key)); }
    public void delete(String key) { c0.remove(key.getBytes()); }
    private boolean hasKey(String key) { return cms.getEstimatedCountString(key) > 0; }
}